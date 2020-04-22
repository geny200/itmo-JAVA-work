package ru.ifmo.rain.konovalov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloUDPClient implements HelloClient {

    class UDPClient extends UDPSocketWorker {
        private final BlockingQueue<String> result;
        private final ArrayList<DatagramSocket> sockets;
        private final ArrayList<DatagramPacket> packets;
        private final String prefix;
        private final int requests;
        private final AtomicInteger workers;

        UDPClient(SocketAddress address, int threads, int requests, String prefix) throws SocketException {
            super(threads);
            this.requests = requests;
            this.prefix = prefix;
            this.sockets = new ArrayList<>();
            this.packets = new ArrayList<>();
            try {
                for (int i = 0; i != threads; ++i) {
                    DatagramSocket localSocket = new DatagramSocket();
                    localSocket.setSoTimeout(2000);
                    this.sockets.add(localSocket);
                    this.packets.add(new DatagramPacket(new byte[0], 0, 0, address));
                }
            } catch (SocketException e) {
                for (DatagramSocket socket : this.sockets)
                    socket.close();
                throw e;
            }
            this.result = new LinkedBlockingQueue<>();
            this.workers = new AtomicInteger();
            this.workers.set(threads);
        }

        @Override
        protected void work(int number) {
            DatagramSocket socket;
            DatagramPacket packet;
            synchronized (sockets) {
                socket = sockets.get(number);
                packet = packets.get(number);
            }

            try {
                for (int i = 0; i != requests; ++i) {
                    String sendMessage = prefix + (number) + "_" + i;
                    byte[] sendData = sendMessage.getBytes(StandardCharsets.UTF_8);

                    packet.setData(sendData, 0, sendData.length);
                    result.put("Thread - " + number + "\n"
                            + "Send:    " + sendMessage + "\n"
                            + "Receive: " + request(socket, packet, sendMessage));
                }
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            } catch (IOException ignore) {
                // socket was closed
            } finally {
                synchronized (sockets) {
                    if (!sockets.get(number).isClosed())
                        sockets.get(number).close();
                    workers.decrementAndGet();
                }
            }
        }

        protected String request(final DatagramSocket socket, DatagramPacket packet, String message) throws IOException {
            int bufferSize = socket.getReceiveBufferSize();
            DatagramPacket receivePacket = new DatagramPacket(new byte[bufferSize], bufferSize);

            while (true) {
                try {
                    socket.send(packet);
                    try {
                        socket.receive(receivePacket);
                        String answer = new String(
                                receivePacket.getData(),
                                receivePacket.getOffset(),
                                receivePacket.getLength(),
                                StandardCharsets.UTF_8);
                        if (answer.endsWith(message)
                                && answer.startsWith("Hello, ")
                                && answer.length() == (message.length() + "Hello, ".length()))
                            return answer;
                        System.out.println("resend");
                    } catch (SocketTimeoutException ignore) {

                    }
                } catch (IOException e) {
                    if (socket.isClosed())
                        throw e;
                }
            }
        }

        void print() {
            start();
            try {
                while (workers.get() > 0 || result.size() > 0) {
                    String line = result.take();
                    if (workers.get() < 0)
                        return;
                    System.out.println(line);
                }
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void close() {
            for (DatagramSocket socket : this.sockets)
                if (!socket.isClosed())
                    socket.close();
            super.close();
            workers.set(0);
            try {
                result.put(" ");
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        HelloUDPClient helloUDPServer = new HelloUDPClient();
        helloUDPServer.run("localhost", 49741, "Prefix", 1, 1);
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
        System.out.println("Client starts with parameters " + "\n"
                + "host:        " + host + "\n"
                + "port:        " + port + "\n"
                + "prefix:      " + prefix + "\n"
                + "threads:     " + threads + "\n"
                + "requests:    " + requests);

        SocketAddress socketAddress;
        try {
            InetAddress hostAddress;
            hostAddress = InetAddress.getByName(host);
            socketAddress = new InetSocketAddress(hostAddress, port);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid host name - " + e.getMessage());
        }
        try (UDPClient client = new UDPClient(socketAddress, threads, requests, prefix)) {
            client.print();
        } catch (SocketException e) {
            throw new IllegalStateException("Socket error - " + e.getMessage());
        }
        System.out.println("Client finished");
    }
}
