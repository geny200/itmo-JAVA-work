package ru.ifmo.rain.konovalov.bank;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client implements BankClient {
    private BankServerImpl bankServer;
    private Bank bank;
    private final AtomicBoolean startFlag;

    public Client() {
        this.startFlag = new AtomicBoolean();
    }

    public void start(int port) {
        if (port <= 1023)
            throw new IllegalArgumentException("Ports less than 1023 are reserved");
        if (this.startFlag.compareAndExchange(false, true))
            throw new IllegalStateException("Client was started");
        bankServer = new BankServerImpl();
        bankServer.start(port);
        try {
            Registry registry = LocateRegistry.getRegistry(null, port);
            bank = (Bank) registry.lookup("//localhost/bank");
        } catch (NotBoundException e) {
            throw new IllegalStateException("Server wasn't started on port: " + port);
        } catch (RemoteException e) {
            throw new IllegalArgumentException("Invalid port");
        }
    }

    @Override
    public int change(String name, String surname, String passport, String accountName, String modification) throws RemoteException {
        Person person = bank.createPerson(name, surname, passport);
        if (!name.equals(person.getName()) || !surname.equals(person.getSurname()))
            return 0;
        Account account = bank.createPersonAccount(accountName, person).getAccount(accountName);
        account.setAmount(account.getAmount() + Integer.parseInt(modification));
        return account.getAmount();
    }

    public static void main(String[] args) {
        if (args == null || args.length != 5) {
            System.out.println("run with options - name surname passport account modification");
            return;
        }
        for (int i = 0; i != 5; ++i) {
            if (args[i] == null) {
                System.out.println("Invalid arguments");
                return;
            }
        }

        try (Client client = new Client()) {
            client.start(28887);
            System.out.println(client.change(args[0], args[1], args[2], args[3], args[4]));
        } catch (RemoteException e) {
            System.out.println("RemoteException: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        bank = null;
        bankServer.close();
    }
}
