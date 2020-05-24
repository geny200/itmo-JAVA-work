package ru.ifmo.rain.konovalov.bank;

import ru.ifmo.rain.common.bank.Account;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteAccount extends UnicastRemoteObject implements Account {
    private final String id;
    private int amount;

    public RemoteAccount(String id) throws RemoteException {
        super();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    synchronized public int getAmount() {
        return amount;
    }

    synchronized public void setAmount(int amount) {
        this.amount = amount;
    }

    synchronized public LocalAccount getLocalAccount() {
        LocalAccount localAccount = new LocalAccount(id);
        localAccount.setAmount(amount);
        return localAccount;
    }
}
