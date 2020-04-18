package ru.ifmo.rain.konovalov.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ScalarIP;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementation of {@link ScalarIP} interface
 * (HW 7)
 *
 * @author Geny200
 */
public class IterativeParallelism7HW implements ScalarIP {

    private static class ResultFlag<T> {
        T flag;

        /**
         * Initializes the class with parameter init.
         *
         * @param init saved value upon initialization
         */
        ResultFlag(T init) {
            flag = init;
        }

        /**
         * Synchronizes the preservation of values.
         *
         * @param value synchronizes save to value
         */
        public synchronized void raise(T value) {
            flag = value;
        }

        /**
         * Returns the saved value.
         *
         * @return saved value
         */
        public T get() {
            return flag;
        }
    }

    private <T, R> void start(int threads,
                              List<? extends T> values,
                              Function<Spliterator<? extends T>, R> init,
                              BiFunction<Spliterator<? extends T>, R, Boolean> loop,
                              Consumer<R> synchronize)
            throws InterruptedException, IllegalArgumentException {
        if (threads <= 0)
            throw new IllegalArgumentException("threads can't be less than or equal to zero");

        int part = values.size() / threads;
        List<Thread> threadList = IntStream.range(0, threads)
                .mapToObj(i -> {
                            if (i != threads - 1) {
                                return values.subList(i * part, (i + 1) * part);
                            }
                            return values.subList(i * part, values.size());
                        }
                )
                .map(List::spliterator)
                .map(spliterator -> new Thread(() -> {
                    R tempValue = init.apply(spliterator);
                    while (loop.apply(spliterator, tempValue))
                        if (Thread.interrupted()) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    synchronize.accept(tempValue);
                }))
                .peek(Thread::start).collect(Collectors.toList());
        try {
            for (Thread thread : threadList)
                thread.join();
        } catch (InterruptedException e) {
            for (Thread thread : threadList)
                if (thread.isAlive())
                    thread.interrupt();
            throw e;
        }
    }

    /**
     * Returns maximum value.
     *
     * @param threads    number or concurrent threads.
     * @param values     values to get maximum of.
     * @param comparator value comparator.
     * @return maximum of given values
     * @throws InterruptedException     if executing thread was interrupted.
     * @throws NoSuchElementException   if not values are given.
     * @throws IllegalArgumentException if threads <= 0
     */
    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException, NoSuchElementException, IllegalArgumentException {
        if (threads <= 0)
            throw new IllegalArgumentException("threads can't be less than or equal to zero");
        if (values == null || values.isEmpty())
            throw new NoSuchElementException("missing search range (values)");

        ResultFlag<T> result = new ResultFlag<T>(values.get(0));
        start(threads, values,
                spliterator -> new ResultFlag<>(result.get()),
                (spliterator, localResult) -> spliterator.tryAdvance(o -> {
                    if (comparator.compare(o, localResult.get()) > 0)
                        localResult.raise(o);
                }),
                localResult -> {
                    synchronized (result) {
                        if (comparator.compare(localResult.get(), result.get()) > 0)
                            result.raise(localResult.get());
                    }
                }
        );
        return result.get();
    }

    /**
     * Returns minimum value.
     *
     * @param threads    number or concurrent threads.
     * @param values     values to get minimum of.
     * @param comparator value comparator.
     * @return minimum of given values
     * @throws InterruptedException     if executing thread was interrupted.
     * @throws NoSuchElementException   if not values are given.
     * @throws IllegalArgumentException if threads <= 0
     */
    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException, NoSuchElementException, IllegalArgumentException {
        return maximum(threads, values, comparator.reversed());
    }

    /**
     * Returns whether all values satisfies predicate.
     *
     * @param threads   number or concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @return whether all values satisfies predicate or {@code true}, if no values are given.
     * @throws InterruptedException     if executing thread was interrupted.
     * @throws IllegalArgumentException if threads <= 0
     */
    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException, NoSuchElementException, IllegalArgumentException {
        if (values.isEmpty())
            return true;

        ResultFlag<Boolean> result = new ResultFlag<>(false);
        start(threads, values,
                spliterator -> true,
                (spliterator, aBoolean) -> spliterator.tryAdvance(o -> {
                    if (!predicate.test(o))
                        result.raise(true);
                }) && !result.get(),
                aBoolean -> {
                }
        );

        return !result.get();
    }

    /**
     * Returns whether any of values satisfies predicate.
     *
     * @param threads   number or concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @return whether any value satisfies predicate or {@code false}, if no values are given.
     * @throws InterruptedException     if executing thread was interrupted.
     * @throws IllegalArgumentException if threads <= 0
     */
    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException, NoSuchElementException, IllegalArgumentException {
        return !all(threads, values, predicate.negate());
    }
}