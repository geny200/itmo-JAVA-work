package ru.ifmo.rain.konovalov.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementation of {@link ParallelMapper} interface
 * (HW 8)
 *
 * @author Geny200
 */
public class ParallelMapperImpl implements ParallelMapper {
    private final List<Thread> threadStream;
    private final Queue<MyFunction<?>> functionQueue;
    private int aClose;

    private static class ResultCollector<T> {
        final ArrayList<T> resultList;
        int cnt;

        ResultCollector(ArrayList<T> resultList) {
            this.cnt = 0;
            this.resultList = resultList;
        }

        synchronized void set(int index, T value) {
            resultList.set(index, value);
            if (resultList.size() == ++cnt)
                notify();
        }

        synchronized boolean isFull() {
            return resultList.size() == cnt;
        }
    }

    private static class MyFunction<R> {
        final Supplier<R> rSupplier;
        final Consumer<R> rConsumer;

        MyFunction(Supplier<R> rSupplier, Consumer<R> rConsumer) {
            this.rSupplier = rSupplier;
            this.rConsumer = rConsumer;
        }

        public void run() throws InterruptedException {
            R localResult = rSupplier.get();
            if (Thread.interrupted())
                throw new InterruptedException();
            rConsumer.accept(localResult);
        }
    }

    public ParallelMapperImpl(int threads) {
        this.aClose = threads;
        this.functionQueue = new LinkedList<>();
        this.threadStream = IntStream.range(0, threads).mapToObj(i -> new Thread(() -> {
            try {
                while (true) {
                    MyFunction<?> func;
                    synchronized (functionQueue) {
                        if (Thread.interrupted()) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                        while (functionQueue.isEmpty()) {
                            functionQueue.wait();
                            if (Thread.interrupted()) {
                                Thread.currentThread().interrupt();
                                functionQueue.notify();
                                return;
                            }
                        }
                        func = functionQueue.remove();
                        functionQueue.notify();
                    }
                    if (Thread.interrupted()) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    try {
                        func.run();
                    } catch (Exception ignored) {
                    }
                }
            } catch (InterruptedException ignored) {
            }
            --aClose;
        })).peek(Thread::start).collect(Collectors.toList());
    }

    /**
     * Maps function {@code f} over specified {@code args}.
     * Mapping for each element performs in parallel.
     *
     * @param f    function
     * @param args list arguments
     * @throws InterruptedException if calling thread was interrupted
     */
    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        if (aClose <= 0)
            throw new IllegalStateException("All threads completed execution.");
        ArrayList<R> resultList = new ArrayList<>(Collections.nCopies(args.size(), null));
        ResultCollector<R> resultCollector = new ResultCollector<>(resultList);

        final Queue<MyFunction<?>> linkedList = IntStream
                .range(0, args.size())
                .mapToObj(i -> new MyFunction<R>(
                        () -> f.apply(args.get(i)),
                        (R value) -> resultCollector.set(i, value)))
                .collect(Collectors.toCollection(LinkedList::new));

        synchronized (functionQueue) {
            functionQueue.addAll(linkedList);
            functionQueue.notify();
        }

        synchronized (resultCollector) {
            while (!resultCollector.isFull() && aClose >= 0) {
                if (Thread.interrupted()) {
                    Thread.currentThread().interrupt();
                    throw new InterruptedException();
                }
                resultCollector.wait();
            }
        }
        return resultList;
    }

    /**
     * Stops all threads. All unfinished mappings leave in undefined state.
     */
    @Override
    public synchronized void close() {
        aClose = 0;
        boolean interrupt = Thread.interrupted();
        List<Thread> threadList = threadStream.stream().filter(Thread::isAlive).peek(Thread::interrupt).collect(Collectors.toList());
        synchronized (functionQueue) {
            functionQueue.notifyAll();
        }

        for (Thread thread : threadList) {
            while (thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    interrupt = true;
                    Thread.interrupted();
                    thread.interrupt();
                }
            }
        }
        if (interrupt)
            Thread.currentThread().interrupt();
    }
}
