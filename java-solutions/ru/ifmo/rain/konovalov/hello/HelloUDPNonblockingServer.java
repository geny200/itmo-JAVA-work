package ru.ifmo.rain.konovalov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of {@link HelloServer} interface.
 *
 * @author Geny200
 * @see HelloServer
 * @see info.kgeorgiy.java.advanced.hello.HelloServer
 */
public class HelloUDPNonblockingServer implements HelloServer {
    final private int UDP_SIZE = 65536;
    AtomicBoolean startFlag;
    AtomicBoolean isClose;
    ByteBuffer byteBuffer;

    public HelloUDPNonblockingServer() {
        this.startFlag = new AtomicBoolean(false);
        this.isClose = new AtomicBoolean(false);
        this.byteBuffer = ByteBuffer.allocate(UDP_SIZE);
    }

    private void send(SelectionKey selectionKey) {
        try {
            if (selectionKey.isReadable()) {
                ReadableByteChannel readableByteChannel = (ReadableByteChannel) selectionKey.channel();
                WritableByteChannel writableByteChannel = (WritableByteChannel) selectionKey.channel();
                byteBuffer.clear();
                int read = readableByteChannel.read(byteBuffer);
                if (read > 0) {
                    String sendMessage = "Hello, " + new String(
                            byteBuffer.array(),
                            0,
                            read,
                            StandardCharsets.UTF_8);
                    byte[] sendData = sendMessage.getBytes(StandardCharsets.UTF_8);
                    writableByteChannel.write(ByteBuffer.wrap(sendData, 0, sendData.length));
                }
                selectionKey.interestOps(SelectionKey.OP_WRITE);
            }
        } catch (IOException e) {
            if (!selectionKey.channel().isOpen())
                selectionKey.cancel();
        }
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
            DatagramChannel channel;
            try {
                channel = DatagramChannel.open();
                channel.socket().bind(new InetSocketAddress(port));
                channel.configureBlocking(false);
                channel.socket().setReceiveBufferSize(UDP_SIZE);
            } catch (IOException e) {
                throw new IllegalArgumentException("DatagramChannel error - " + e.getMessage());// todo - rename exception
            }
            while (!isClose.get()) {
                SocketAddress socketAddress = channel.receive(byteBuffer);
                if (socketAddress != null) {
                    String answer = "Hello, " + new String(
                            byteBuffer.array(),
                            byteBuffer.arrayOffset(),
                            byteBuffer.position(),
                            StandardCharsets.UTF_8);
                    channel.send(ByteBuffer.wrap(answer.getBytes(StandardCharsets.UTF_8)), socketAddress);
                    byteBuffer.clear();
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Selector error - " + e.getMessage());
        }
        this.startFlag.set(false);
        this.isClose.set(false);
    }

    /**
     * Stops server and deallocates all resources.
     */
    @Override
    public void close() {
        isClose.set(true);
    }
}
