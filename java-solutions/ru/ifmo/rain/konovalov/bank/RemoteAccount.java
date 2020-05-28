package ru.ifmo.rain.konovalov.bank;

import ru.ifmo.test.common.bank.Account;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Eugene Geny200
 * @see ru.ifmo.test.common.bank.Person
 */
public class RemoteAccount extends UnicastRemoteObject implements Account {
    private final String id;
    private int amount;

    /**
     * Construct a new RemoteAccount.
     */
    public RemoteAccount(String id) throws RemoteException {
        super();
        this.id = id;
    }

    /**
     * @return {@link String} - account identifier
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @return - money in the account
     */
    @Override
    synchronized public int getAmount() {
        return amount;
    }

    /**
     * @param amount - new amount of money in the account
     */
    @Override
    synchronized public void setAmount(int amount) {
        this.amount = amount;
    }

    synchronized protected LocalAccount getLocalAccount() {
        LocalAccount localAccount = new LocalAccount(id);
        localAccount.setAmount(amount);
        return localAccount;
    }
}
