package ru.ifmo.rain.konovalov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;
//"C:\Program Files\JetBrains\IntelliJ IDEA 2019.3.1\jbr\bin\java.exe" -cp . -p . -m info.kgeorgiy.java.advanced.hello client ru.ifmo.rain.konovalov.hello.HelloUDPNonblockingClient
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of {@link HelloClient} interface.
 *
 * @author Geny200
 * @see HelloClient
 * @see info.kgeorgiy.java.advanced.hello.HelloClient
 */
public class HelloUDPNonblockingClient implements HelloClient {
    final private int UDP_SIZE = 65536;
    private String prefix;
    private ByteBuffer byteBuffer;
    private int workerClose;

    static private class ContexData {
        private final int threadNumber;
        private final int maxNumber;
        private int iterationNumber;

        ContexData(int number, int maxNumber) {
            this.threadNumber = number;
            this.maxNumber = maxNumber;
            this.iterationNumber = 0;
        }

        void inc() {
            iterationNumber++;
        }

        int iteration() {
            return iterationNumber;
        }

        int number() {
            return threadNumber;
        }

        boolean isEnd() {
            return iterationNumber >= maxNumber;
        }
    }

    private void receiveSend(SelectionKey selectionKey) {
        ContexData data = (ContexData) selectionKey.attachment();
        String sendMessage = prefix + (data.number()) + "_" + data.iteration();
        try {
            if (selectionKey.isReadable()) {
                ReadableByteChannel readableByteChannel = (ReadableByteChannel) selectionKey.channel();
                byteBuffer.clear();
                int read = readableByteChannel.read(byteBuffer);
                if (read > 0) {
                    String answer = new String(
                            byteBuffer.array(),
                            0,
                            read,
                            StandardCharsets.UTF_8);
                    if (answer.endsWith(sendMessage)
                            && answer.startsWith("Hello, ")
                            && answer.length() == (sendMessage.length() + "Hello, ".length())) {
                        data.inc();
                    }
                }
                selectionKey.interestOps(SelectionKey.OP_WRITE);
            } else {
                if (selectionKey.isWritable()) {
                    if (data.isEnd()) {
                        ++this.workerClose;
                        selectionKey.cancel();
                        selectionKey.channel().close();
                        return;
                    }
                    WritableByteChannel writableByteChannel = (WritableByteChannel) selectionKey.channel();
                    byte[] sendData = sendMessage.getBytes(StandardCharsets.UTF_8);
                    writableByteChannel.write(ByteBuffer.wrap(sendData, 0, sendData.length));
                    selectionKey.interestOps(SelectionKey.OP_READ);
                }
            }

        } catch (IOException e) {
            if (!selectionKey.channel().isOpen())
                selectionKey.cancel();
        }
    }

    /**
     * Runs Hello client.
     *
     * @param host     server host
     * @param port     server port
     * @param prefix   request prefix
     * @param threads  number of request threads
     * @param requests number of requests per thread.
     */
    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        SocketAddress socketAddress;
        this.workerClose = 0;
        this.prefix = prefix;
        byteBuffer = ByteBuffer.allocate(UDP_SIZE);
        try (Selector selector = Selector.open()) {
            try {
                InetAddress hostAddress;
                hostAddress = InetAddress.getByName(host);
                socketAddress = new InetSocketAddress(hostAddress, port);
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException("Invalid host name - " + e.getMessage());
            }

            try {
                for (int i = 0; i != threads; ++i) {
                    DatagramChannel channel = DatagramChannel.open();
                    channel.configureBlocking(false);
                    channel.connect(socketAddress);
                    channel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, new ContexData(i, requests));
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("DatagramChannel error - " + e.getMessage());// todo - rename exception
            }
            while (this.workerClose != threads) {
                if (selector.select(this::receiveSend, 500) == 0) {
                    for (SelectionKey selectionKey : selector.keys()) {
                        selectionKey.interestOps(SelectionKey.OP_WRITE);
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Selector error - " + e.getMessage());
        }
    }
}
