package ru.ifmo.rain.konovalov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of {@link HelloServer} interface.
 *
 * @author Geny200
 * @see HelloServer
 * @see info.kgeorgiy.java.advanced.hello.HelloServer
 */
public class HelloUDPNonblockingServer implements HelloServer {
    Selector selector;
    DatagramChannel channel;
    ExecutorService executorService;
    final private int UDP_SIZE = 65536;
    AtomicBoolean startFlag;
    AtomicBoolean isClose;
    ByteBuffer byteBuffer;

    /**
     * Constructs a new HelloUDPNonblockingServer.
     */
    public HelloUDPNonblockingServer() {
        this.startFlag = new AtomicBoolean(false);
        this.isClose = new AtomicBoolean(false);
        this.byteBuffer = ByteBuffer.allocate(UDP_SIZE);
    }

    private void send(SelectionKey selectionKey) {
        try {
            if (selectionKey.isReadable()) {
                SocketAddress address = channel.receive(byteBuffer);
                if (address != null) {
                    String sendMessage = "Hello, " + new String(
                            byteBuffer.array(),
                            0,
                            byteBuffer.position(),
                            StandardCharsets.UTF_8);
                    byte[] sendData = sendMessage.getBytes(StandardCharsets.UTF_8);
                    channel.send(ByteBuffer.wrap(sendData, 0, sendData.length), address);
                    byteBuffer.clear();
                }
            }
        } catch (IOException e) {
            if (!selectionKey.channel().isOpen())
                selectionKey.cancel();
        }
    }

    private void execute() {
        try {
            while (!isClose.get()) {
                selector.select(this::send, 1000);
            }
        } catch (IOException ignore) {

        }
        this.startFlag.set(false);
        this.isClose.set(false);
    }

    /**
     * Starts a new Hello server.
     *
     * @param port    server port.
     * @param threads number of working threads.
     */
    @Override
    public void start(int port, int threads) {
        if (port <= 1023)
            throw new IllegalArgumentException("Ports less than 1023 are reserved");
        if (threads <= 0)
            throw new IllegalArgumentException("Thread must be greater than 0");
        if (this.startFlag.compareAndExchange(false, true))
            throw new IllegalStateException("Server was started");

        try {
            selector = Selector.open();
            try {
                channel = DatagramChannel.open();
                channel.configureBlocking(false);
                channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                channel.bind(new InetSocketAddress(port));
                channel.register(selector, SelectionKey.OP_READ);
            } catch (IOException e) {
                throw new IllegalArgumentException("DatagramChannel error - " + e.getMessage());// todo - rename exception
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Selector error - " + e.getMessage());
        }

        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this::execute);
    }

    /**
     * Stops server and deallocates all resources.
     */
    @Override
    public void close() {
        isClose.set(true);
        executorService.shutdown();
        try {
            if (executorService.awaitTermination(2000, TimeUnit.MILLISECONDS))
                executorService.shutdownNow();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
