package ru.ifmo.rain.common.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Eugene Geny200
 * @author Egor
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Bank extends Remote {

    /**
     * Creates an account by identifier, if it didn't exist otherwise, returns the existing one.
     *
     * @param id - account identifier
     * @return {@link ru.ifmo.rain.common.bank.Account} - created account or existing.
     */
    Account createAccount(String id)
            throws RemoteException;

    /**
     * Finds an account by ID, if it wasn't found, returns null.
     *
     * @param id - account identifier.
     * @return {@link ru.ifmo.rain.common.bank.Account} - account or null.
     */
    Account getAccount(String id)
            throws RemoteException;

    /**
     * Create an account for a person if it didn't exist.
     *
     * @param accountName  - account identifier.
     * @param remotePerson {@link ru.ifmo.rain.common.bank.Account} - person.
     * @return {@link ru.ifmo.rain.common.bank.Account} - created account or existing.
     */
    Account createPersonAccount(String accountName, Person remotePerson)
            throws RemoteException;

    /**
     * Creates a record of a person if he didn't exist and returns it, otherwise returns the existing one returns. (passport is a unique key)
     *
     * @param name     {@link java.lang.String} - first name of a person.
     * @param surname  {@link java.lang.String} - last name of a person.
     * @param passport {@link java.lang.String} - passport of a person.
     * @return {@link ru.ifmo.rain.common.bank.Person} - created RemotePerson or existing.
     */
    Person createPerson(String name, String surname, String passport)
            throws RemoteException;

    /**
     * Finds a person on the passport, otherwise returns null.
     *
     * @param passport {@link java.lang.String} - passport of a person.]
     * @return {@link ru.ifmo.rain.common.bank.Person} - found LocalPerson or null.
     */
    Person getLocalPerson(String passport)
            throws RemoteException;

    /**
     * Finds a person on the passport, otherwise returns null.
     *
     * @param passport {@link java.lang.String} - passport of a person.]
     * @return {@link ru.ifmo.rain.common.bank.Person} - found RemotePerson or null.
     */
    Person getRemotePerson(String passport)
            throws RemoteException;
}
