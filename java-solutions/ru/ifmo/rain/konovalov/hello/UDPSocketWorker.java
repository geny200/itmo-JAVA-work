package ru.ifmo.rain.konovalov.hello;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

abstract class UDPSocketWorker implements AutoCloseable {
    private final ExecutorService threadPool;
    private final int threads;

    protected UDPSocketWorker(int threads) {
        this.threadPool = Executors.newFixedThreadPool(threads);
        this.threads = threads;
    }

    protected void start() {
        for (int i = 0; i != threads; ++i) {
            int finalI = i;
            threadPool.submit(() -> work(finalI));
        }
    }

    abstract protected void work(int number);

    @Override
    public void close() {
        try {
            threadPool.shutdown();
            if (!threadPool.isTerminated())
                threadPool.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
