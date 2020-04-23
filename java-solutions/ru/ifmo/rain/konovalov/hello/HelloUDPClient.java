package ru.ifmo.rain.konovalov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of {@link HelloClient} interface.
 *
 * @author Geny200
 * @see  HelloClient
 * @see info.kgeorgiy.java.advanced.hello.HelloClient
 */
public class HelloUDPClient implements HelloClient {

    /**
     * Constructs a new HelloUDPClient.
     */
    public HelloUDPClient() {
    }

    static private class UDPClient extends UDPSocketWorker {
        private final BlockingQueue<String> result;
        private final ArrayList<DatagramSocket> sockets;
        private final ArrayList<DatagramPacket> packets;
        private final String prefix;
        private final int requests;
        private final AtomicInteger workers;

        protected UDPClient(SocketAddress address, int threads, int requests, String prefix) throws SocketException {
            super(threads);
            this.requests = requests;
            this.prefix = prefix;
            this.sockets = new ArrayList<>();
            this.packets = new ArrayList<>();
            try {
                for (int i = 0; i != threads; ++i) {
                    DatagramSocket localSocket = new DatagramSocket();
                    localSocket.setSoTimeout(1000);
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
            int bufferSize;

            try {
                synchronized (sockets) {
                    socket = sockets.get(number);
                    packet = packets.get(number);
                    bufferSize = socket.getReceiveBufferSize();
                }

                for (int i = 0; i != requests; ++i) {
                    String sendMessage = prefix + (number) + "_" + i;
                    byte[] sendData = sendMessage.getBytes(StandardCharsets.UTF_8);

                    packet.setData(sendData, 0, sendData.length);
                    result.put("Thread - " + number + "\n"
                            + "Send:    " + sendMessage + "\n"
                            + "Receive: " + request(socket, packet, bufferSize, sendMessage));
                }
            } catch (InterruptedException | IOException ignore) {
                // socket was closed
            } finally {
                synchronized (sockets) {
                    if (!sockets.get(number).isClosed())
                        sockets.get(number).close();
                    workers.decrementAndGet();
                }
            }
        }

        protected String request(final DatagramSocket socket, DatagramPacket packet, int receiveBufferSize, String message) throws IOException {
            byte[] buffer = new byte[receiveBufferSize];
            DatagramPacket receivePacket = new DatagramPacket(buffer, 0, buffer.length);

            while (true) {
                while (true) {
                    try {
                        socket.send(packet);
                        break;
                    } catch (IOException e) {
                        if (socket.isClosed())
                            throw e;
                    }
                }
                while (true) {
                    try {
                        receivePacket.setData(buffer, 0, buffer.length);
                        socket.receive(receivePacket);
                        String answer = new String(
                                receivePacket.getData(),
                                receivePacket.getOffset(),
                                receivePacket.getLength(),
                                StandardCharsets.UTF_8);
                        if (receivePacket.getSocketAddress().equals(packet.getSocketAddress())
                                && answer.endsWith(message)
                                && answer.startsWith("Hello, ")
                                && answer.length() == (message.length() + "Hello, ".length()))
                            return answer;
                        System.out.println("resend");
                    } catch (SocketTimeoutException ignore) {
                        break;
                    } catch (IOException e) {
                        if (socket.isClosed())
                            throw e;
                    }
                }
            }
        }

        public void print() throws InterruptedException {
            start();
            while (workers.get() > 0 || result.size() > 0) {
                String line = result.take();
                if (workers.get() < 0)
                    return;
                System.out.println(line);
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

    /**
     * Start HelloUDPClient.
     * Use to start:
     * <ul>
     *         <li> {@code HelloUDPClient host port prefix threads requests}
     *         calls {@link HelloUDPClient#run(String, int, String, int, int)}
     *         </li>
     * </ul>
     *
     * @param args array of input parameters ({@link java.lang.String}).
     * @see HelloUDPClient#run(String, int, String, int, int)
     */
    public static void main(String[] args) {
        if (args == null || args.length != 5) {
            System.out.println("Input arguments should be 5: host, port, prefix, threads, requests");
            return;
        }
        for (int i = 0; i != 5; ++i) {
            if (args[i] == null) {
                System.out.println("Invalid input argument " + i + " - null argument)");
                return;
            }
        }
        int port = Integer.parseInt(args[1]), threads = Integer.parseInt(args[3]), requests = Integer.parseInt(args[4]);
        HelloUDPClient helloUDPServer = new HelloUDPClient();
        try {
            helloUDPServer.run(args[0], port, args[2], threads, requests);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid argument: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
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
        if (port <= 1023)
            throw new IllegalArgumentException("Ports less than 1023 are reserved");
        if (threads <= 0)
            throw new IllegalArgumentException("Thread must be greater than 0");
        if (requests < 0)
            throw new IllegalArgumentException("Thread must be eq or greater than 0");
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
        } catch (InterruptedException ignore) {
            System.out.println("Client interrupted");
            Thread.currentThread().interrupt();
        }
        System.out.println("Client finished");
    }
}
