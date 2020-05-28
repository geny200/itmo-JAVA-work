package ru.ifmo.rain.konovalov.bank;

import ru.ifmo.test.common.bank.Account;
import ru.ifmo.test.common.bank.Bank;
import ru.ifmo.test.common.bank.BankClient;
import ru.ifmo.test.common.bank.Person;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client implements BankClient {
    private BankServerImpl bankServer;
    private Bank bank;
    private final AtomicBoolean startFlag;

    /**
     * Construct a new Client.
     */
    public Client() {
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

    /**
     * Change the amount of the account and returns a new amount.
     *
     * @param name         {@link String} - first name of a person.
     * @param surname      {@link String} - last name of a person.
     * @param passport     {@link String} - passport of a person.
     * @param accountName  {@link String} - person account id.
     * @param modification {@link String} - change in invoice amount.
     * @return - returns the amount of money in a person’s account.
     * @see Person
     * @see Account
     */
    @Override
    public int change(String name, String surname, String passport, String accountName, String modification) throws RemoteException {
        Person person = bank.createPerson(name, surname, passport);
        if (!name.equals(person.getName()) || !surname.equals(person.getSurname()))
            return 0;
        Account account = bank.createPersonAccount(accountName, person).getAccount(accountName);
        account.setAmount(account.getAmount() + Integer.parseInt(modification));
        return account.getAmount();
    }

    /**
     * Stops server and deallocate all resources.
     */
    @Override
    public void close() {
        bank = null;
        bankServer.close();
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


}
