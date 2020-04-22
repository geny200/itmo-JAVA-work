package ru.ifmo.rain.konovalov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

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
    public HelloUDPServer() {
        udpServer = null;
    }

    class UDPServer extends UDPSocketWorker {
        final DatagramSocket socketUDP;
        final int bufferReceiveSize;

        UDPServer(DatagramSocket socket, int threads) throws SocketException {
            super(threads);
            this.socketUDP = socket;
            this.bufferReceiveSize = socketUDP.getReceiveBufferSize();
            start();
        }

        @Override
        protected void work(int number) {
            DatagramPacket packet = new DatagramPacket(new byte[bufferReceiveSize], bufferReceiveSize);
            try {
                while (true) {
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
            super.close();
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
        if (udpServer != null)
            new IllegalStateException("Server was started");
        try {
            DatagramSocket socket = new DatagramSocket(port);
            udpServer = new UDPServer(socket, threads);
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
