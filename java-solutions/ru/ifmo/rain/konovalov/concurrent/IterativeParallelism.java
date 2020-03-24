package ru.ifmo.rain.konovalov.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;

/**
 * Implementation of {@link ScalarIP} interface
 * HW 8
 *
 * @author Geny200
 */
public class IterativeParallelism implements ScalarIP {
    private final ParallelMapper threadPool;

    /**
     * Initializes the class with parameter threadPool.
     *
     * @param threadPool type of {@link ParallelMapper}.
     */
    public IterativeParallelism(ParallelMapper threadPool) {
        this.threadPool = threadPool;
    }

    /**
     * Default constructor
     */
    public IterativeParallelism() {
        this(null);
    }

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

    private <T> T maximumExe(ParallelMapper parallelMapper, List<Spliterator<? extends T>> values, Comparator<? super T> comparator, T init) throws InterruptedException {
        ResultFlag<T> result = new ResultFlag<T>(init);
        parallelMapper.map((Spliterator<? extends T> spliterator) -> {
            ResultFlag<T> localResult = new ResultFlag<>(result.get());
            while (spliterator.tryAdvance(o -> {
                if (comparator.compare(o, localResult.get()) > 0)
                    localResult.raise(o);
            })) {
                if (Thread.interrupted()) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            synchronized (result) {
                if (comparator.compare(localResult.get(), result.get()) > 0)
                    result.raise(localResult.get());
            }
            return false;
        }, values);
        return result.get();
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

        ArrayList<Spliterator<? extends T>> listPart = new ArrayList<>(Collections.nCopies(threads, null));
        for (int part = values.size() / threads, left = 0, extra = values.size() % threads, i = 0; i != threads; ++i) {
            int right = left + part;
            if (extra > 0) {
                --extra;
                ++right;
            }
            listPart.set(i, values.subList(left, Math.min(right, values.size())).spliterator());
            left = right;
        }

        if (threadPool == null)
            try (ParallelMapper parallelMapper = new ParallelMapperImpl(threads)) {
                return maximumExe(parallelMapper, listPart, comparator, values.get(0));
            }
        return maximumExe(threadPool, listPart, comparator, values.get(0));
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

    private <T> boolean allExe(ParallelMapper parallelMapper, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        ResultFlag<Boolean> result = new ResultFlag<>(false);
        parallelMapper.map((T value) -> {
            if (!result.get() && !predicate.test(value)) {
                result.raise(true);
            }
            return false;
        }, values);
        return !result.get();
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

        if (threadPool == null)
            try (ParallelMapper parallelMapper = new ParallelMapperImpl(threads)) {
                return allExe(parallelMapper, values, predicate);
            }
        return allExe(threadPool, values, predicate);
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