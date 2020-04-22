package ru.ifmo.rain.konovalov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link HelloServer} interface.
 *
 * @author Geny200
 * @see HelloServer
 * @see info.kgeorgiy.java.advanced.hello.HelloServer
 */
public class HelloUDPServer implements HelloServer {
    UDPServer udpServer;

    /**
     * Constructs a new HelloUDPServer.
     */
    HelloUDPServer() {
        udpServer = null;
    }

    class UDPServer implements AutoCloseable {
        final DatagramSocket socketUDP;
        final ExecutorService threadPool;
        final int bufferReceiveSize;

        UDPServer(int port, int threads) throws SocketException {
            this.socketUDP = new DatagramSocket(port);
            this.bufferReceiveSize = socketUDP.getReceiveBufferSize();
            this.threadPool = Executors.newFixedThreadPool(threads);
            for (int i = 0; i != threads; ++i)
                threadPool.submit(this::work);
        }

        void work() {
            try {
                while (true) {
                    DatagramPacket packet = new DatagramPacket(new byte[bufferReceiveSize], bufferReceiveSize);
                    socketUDP.receive(packet);
                    String sendMessage = "Hello, " + new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                    byte[] sendData = sendMessage.getBytes(StandardCharsets.UTF_8);
                    if (sendData.length > socketUDP.getSendBufferSize()) {
                        synchronized (socketUDP) {
                            if (sendData.length > socketUDP.getSendBufferSize()) {
                                socketUDP.setSendBufferSize(sendData.length);
                            }
                        }
                        packet.setData(sendData);
                    } else {
                        for (int i = 0; i != sendData.length; ++i)
                            packet.getData()[i] = sendData[i];
                        packet.setLength(sendData.length);
                    }
                    socketUDP.send(packet);
                }
            } catch (IOException ignore) {
            }
        }

        @Override
        public void close() {
            socketUDP.close();
            try {
                threadPool.shutdown();
                threadPool.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {

    }

    /**
     * Starts a new Hello server.
     *
     * @param port    server port.
     * @param threads number of working threads.
     */
    @Override
    public void start(int port, int threads) {
        if (udpServer != null)
            new IllegalStateException("Server was started");
        try {
            udpServer = new UDPServer(port, threads);
        } catch (SocketException e) {
            udpServer = null;
            e.printStackTrace();
        }
    }

    /**
     * Stops server and deallocates all resources.
     */
    @Override
    public void close() {
        if (udpServer != null) {
            udpServer.close();
        }
    }
}
