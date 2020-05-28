package ru.ifmo.rain.konovalov.bank;

import ru.ifmo.test.common.bank.Account;
import ru.ifmo.test.common.bank.Bank;
import ru.ifmo.test.common.bank.Person;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eugene Geny200
 * @see ru.ifmo.test.common.bank.Bank
 */
class RemoteBank implements Bank {
    private final ConcurrentHashMap<String, RemoteAccount> accounts;
    private final ConcurrentHashMap<String, RemotePerson> persons;

    /**
     * Construct a new RemoteBank.
     */
    RemoteBank() {
        this.accounts = new ConcurrentHashMap<>();
        this.persons = new ConcurrentHashMap<>();
    }

    /**
     * Finds a person on the passport, otherwise returns null.
     *
     * @param passport {@link String} - passport of a person.]
     * @return {@link Person} - found LocalPerson or null.
     */
    @Override
    public LocalPerson getLocalPerson(String passport) {
        RemotePerson person = persons.get(passport);
        if (person == null)
            return null;
        return person.getLocalPerson();
    }

    /**
     * Finds a person on the passport, otherwise returns null.
     *
     * @param passport {@link String} - passport of a person.]
     * @return {@link Person} - found RemotePerson or null.
     */
    @Override
    public Person getRemotePerson(String passport) {
        return persons.get(passport);
    }

    /**
     * Creates an account by identifier, if it didn't exist otherwise, returns the existing one.
     *
     * @param id - account identifier
     * @return {@link Account} - created account or existing.
     */
    @Override
    public Account createAccount(String id) throws RemoteException {
        RemoteAccount account = new RemoteAccount(id);
        accounts.put(id, account);
        return account;
    }

    /**
     * Finds an account by ID, if it wasn't found, returns null.
     *
     * @param id - account identifier.
     * @return {@link Account} - account or null.
     */
    @Override
    public Account getAccount(String id) {
        return accounts.get(id);
    }

    /**
     * Create an account for a person if it didn't exist.
     *
     * @param accountName - account identifier.
     * @param person      {@link Account} - person.
     * @return {@link Person} - person with an added account if it did not exist.
     */
    @Override
    public Person createPersonAccount(String accountName, Person person) throws RemoteException {
        if (person instanceof LocalPerson) {
            LocalPerson localPerson = (LocalPerson) person;
            RemoteAccount account = new RemoteAccount(person.getPassport() + ':' + accountName);
            localPerson.setAccount(account);
            return localPerson;
        }
        RemotePerson remotePerson = persons.get(person.getPassport());
        RemoteAccount account;

        String id = remotePerson.getPassport() + ':' + accountName;
        account = new RemoteAccount(id);
        RemoteAccount accountPresent = accounts.putIfAbsent(id, account);
        if (accountPresent != null)
            account = accountPresent;
        remotePerson.setAccount(account);

        return remotePerson;
    }

    /**
     * Creates a record of a person if he didn't exist and returns it, otherwise returns the existing one returns. (passport is a unique key)
     *
     * @param name     {@link String} - first name of a person.
     * @param surname  {@link String} - last name of a person.
     * @param passport {@link String} - passport of a person.
     * @return {@link Person} - created RemotePerson or existing.
     */
    @Override
    public Person createPerson(String name, String surname, String passport) throws RemoteException {
        RemotePerson remotePerson = new RemotePerson(name, surname, passport);
        RemotePerson person = persons.putIfAbsent(passport, remotePerson);
        if (person != null)
            return person;
        return remotePerson;
    }
}
