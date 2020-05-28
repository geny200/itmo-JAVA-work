package ru.ifmo.test.common.bank;

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
     * @return {@link Account} - person account.
     */
    Account getAccount(String accountName)
            throws RemoteException;
}
