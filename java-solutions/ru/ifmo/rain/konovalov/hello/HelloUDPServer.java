package ru.ifmo.rain.konovalov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;
//"C:\Program Files\JetBrains\IntelliJ IDEA 2019.3.1\jbr\bin\java.exe" -cp . -p . -m info.kgeorgiy.java.advanced.hello server ru.ifmo.rain.konovalov.hello.HelloUDPServer
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Implementation of {@link HelloServer} interface.
 *
 * @author Geny200
 * @see HelloServer
 * @see info.kgeorgiy.java.advanced.hello.HelloServer
 */
public class HelloUDPServer implements HelloServer {
    private UDPServer udpServer;

    /**
     * Constructs a new HelloUDPServer.
     */
    public HelloUDPServer() {
        udpServer = null;
    }

    static private class UDPServer extends UDPSocketWorker {
        private final DatagramSocket socketUDP;
        private final int bufferReceiveSize;
        private final BlockingQueue<DatagramPacket> packets;
        private final Thread queueThread;

        UDPServer(DatagramSocket socket, int threads) throws SocketException {
            super(threads);
            this.socketUDP = socket;
            this.bufferReceiveSize = socketUDP.getReceiveBufferSize();
            this.packets = new LinkedBlockingQueue<>();
            this.queueThread = new Thread(this::queueWorker);
            this.queueThread.start();
            start();
        }

        private void queueWorker() {
            try {
                while (true) {
                    DatagramPacket packet = new DatagramPacket(new byte[bufferReceiveSize], 0, bufferReceiveSize);
                    try {
                        socketUDP.receive(packet);
                        packets.put(packet);
                    } catch (IOException e) {
                        if (socketUDP.isClosed())
                            return;
                    }
                }
            } catch (InterruptedException ignore) {
            }
        }

        @Override
        protected void work(int number) {
            DatagramPacket packet;
            try {
                while (true) {
                    packet = packets.take();
                    String sendMessage = "Hello, "
                            + new String(
                            packet.getData(),
                            packet.getOffset(),
                            packet.getLength(),
                            StandardCharsets.UTF_8);

                    byte[] sendData = sendMessage.getBytes(StandardCharsets.UTF_8);
                    packet.setData(sendData, 0, sendData.length);

                    while (true) {
                        try {
                            socketUDP.send(packet);
                            break;
                        } catch (IOException ignore) {
                            if (socketUDP.isClosed())
                                return;
                        }
                    }
                }
            } catch (InterruptedException ignore) {
            }
        }

        @Override
        public void close() {
            socketUDP.close();
            queueThread.interrupt();
            while (queueThread.isAlive())
                try {
                    queueThread.join();
                } catch (InterruptedException ignored) {
                }
            super.close();
        }
    }

    /**
     * Start HelloUDPServer.
     * Use to start:
     * <ul>
     *         <li> {@code HelloUDPServer port threads}
     *         calls {@link HelloUDPServer#start(int, int)}
     *         </li>
     * </ul>
     *
     * @param args array of input parameters ({@link java.lang.String}).
     * @see HelloUDPServer#start(int, int)
     */
    public static void main(String[] args) {
        if (args == null || args.length != 2) {
            System.out.println("Input arguments should be 2: port threads");
            return;
        }
        for (int i = 0; i != 2; ++i) {
            if (args[i] == null) {
                System.out.println("Invalid input argument " + i + " - null argument)");
                return;
            }
        }
        int port = Integer.parseInt(args[0]), threads = Integer.parseInt(args[1]);
        try (HelloUDPServer helloUDPServer = new HelloUDPServer()) {
            helloUDPServer.start(port, threads);
            System.out.println("Press Enter to close Server");
            Scanner sc = new Scanner(System.in);
            sc.nextLine();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid argument: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println("Server closed");
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
        if (udpServer != null)
            throw new IllegalStateException("Server was started");
        System.out.println("Server starts with parameters " + "\n"
                + "port:    " + port + "\n"
                + "threads: " + threads);
        try {
            DatagramSocket socket = new DatagramSocket(port);
            udpServer = new UDPServer(socket, threads);
            System.out.println("Server started");
        } catch (SocketException e) {
            udpServer = null;
            throw new IllegalStateException("Socket error: " + e.getMessage());
        }
    }

    /**
     * Stops server and deallocates all resources.
     */
    @Override
    public void close() {
        if (udpServer != null) {
            udpServer.close();
            System.out.println("Server finished");
        }
    }
}
