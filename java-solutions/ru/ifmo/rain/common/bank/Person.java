package ru.ifmo.rain.common.bank;

import ru.ifmo.rain.konovalov.bank.RemoteAccount;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Eugene Geny200
 * @author Egor
 */
public interface Person extends Remote {

    /**
     * @return {@link java.lang.String} - last name of a person.
     */
    String getSurname()
            throws RemoteException;

    /**
     * @return {@link java.lang.String} - passport of a person.
     */
    String getPassport()
            throws RemoteException;

    /**
     * @return {@link java.lang.String} - first name of a person.
     */
    String getName()
            throws RemoteException;

    /**
     * @return {@link ru.ifmo.rain.common.bank.Account} - person account.
     */
    Account getAccount(String accountName)
            throws RemoteException;
}
