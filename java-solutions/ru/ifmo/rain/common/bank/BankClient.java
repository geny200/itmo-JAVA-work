package ru.ifmo.rain.common.bank;

import java.rmi.RemoteException;

/**
 * Client for Bank
 *
 * @author Eugene Geny200
 * @see java.lang.AutoCloseable
 * @see ru.ifmo.rain.common.bank.Bank
 */
public interface BankClient extends AutoCloseable {
    /**
     * Creates an instance of the interface {@link ru.ifmo.rain.common.bank.Bank} and opens RMI on port.
     *
     * @param port - port for RMI.
     * @see ru.ifmo.rain.common.bank.Bank
     */
    void start(int port);

    /**
     * Change the amount of the account and returns a new amount.
     *
     * @param name         {@link java.lang.String} - first name of a person.
     * @param surname      {@link java.lang.String} - last name of a person.
     * @param passport     {@link java.lang.String} - passport of a person.
     * @param accountName  {@link java.lang.String} - person account id.
     * @param modification {@link java.lang.String} - change in invoice amount.
     * @return - returns the amount of money in a person’s account.
     * @see ru.ifmo.rain.common.bank.Person
     * @see ru.ifmo.rain.common.bank.Account
     */
    int change(String name, String surname, String passport, String accountName, String modification) throws RemoteException;

    /**
     * Stops server and deallocate all resources.
     */
    @Override
    void close();
}
