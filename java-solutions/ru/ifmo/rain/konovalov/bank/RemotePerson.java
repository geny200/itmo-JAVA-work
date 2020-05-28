package ru.ifmo.rain.konovalov.bank;

import ru.ifmo.test.common.bank.Account;
import ru.ifmo.test.common.bank.Person;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

public class RemotePerson extends UnicastRemoteObject implements Person {
    private final ConcurrentHashMap<String, RemoteAccount> accounts;
    private final String name;
    private final String surname;
    private final String passport;

    /**
     * Construct a new RemotePerson.
     */
    public RemotePerson(String name, String surname, String passport) throws RemoteException {
        super();
        this.name = name;
        this.surname = surname;
        this.passport = passport;
        this.accounts = new ConcurrentHashMap<>();
    }

    /**
     * @return {@link String} - last name of a person.
     */
    @Override
    public String getSurname() {
        return surname;
    }

    /**
     * @return {@link String} - passport of a person.
     */
    @Override
    public String getPassport() {
        return passport;
    }

    /**
     * @return {@link String} - first name of a person.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param accountName - account identifier
     * @return {@link Account} - person account.
     */
    @Override
    public Account getAccount(String accountName) {
        return accounts.get(passport + ':' + accountName);
    }

    protected void setAccount(RemoteAccount account) {
        accounts.putIfAbsent(account.getId(), account);
    }

    protected LocalPerson getLocalPerson() {
        return new LocalPerson(name, surname, passport, accounts.values().stream());
    }
}
