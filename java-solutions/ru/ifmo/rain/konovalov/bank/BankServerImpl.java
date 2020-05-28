package ru.ifmo.rain.konovalov.bank;

import ru.ifmo.test.common.bank.Bank;
import ru.ifmo.test.common.bank.BankServer;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;

public class BankServerImpl implements BankServer {
    AtomicBoolean startFlag;
    private Bank bank;
    Registry registry;

    /**
     * Constructs a new HelloUDPNonblockingServer.
     */
    public BankServerImpl() {
        this.startFlag = new AtomicBoolean();
    }


    /**
     * Creates an instance of the interface {@link Bank} and opens RMI on port.
     *
     * @param port - port for RMI.
     * @see Bank
     */
    @Override
    public void start(int port) {
        if (port <= 1023)
            throw new IllegalArgumentException("Ports less than 1023 are reserved");
        if (this.startFlag.compareAndExchange(false, true))
            throw new IllegalStateException("Server was started");

        bank = new RemoteBank();
        try {
            Bank stub = (Bank) UnicastRemoteObject.exportObject(bank, 0);
            registry = LocateRegistry.createRegistry(port);
            registry.rebind("//localhost/bank", stub);
        } catch (RemoteException e) {
            startFlag.set(false);
            e.printStackTrace();
            throw new IllegalStateException("Cannot export object: ");
        }
    }

    /**
     * Stops server and deallocate all resources.
     */
    @Override
    public void close() {
        if (registry != null) {
            startFlag.set(false);
            try {
                UnicastRemoteObject.unexportObject(bank, false);
                UnicastRemoteObject.unexportObject(registry, false);
            } catch (NoSuchObjectException ignore) {

            }
            bank = null;
        }
    }


}
