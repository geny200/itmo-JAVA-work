package ru.ifmo.rain.konovalov.bank;

import ru.ifmo.rain.common.bank.Account;
import ru.ifmo.rain.common.bank.Person;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

public class RemotePerson extends UnicastRemoteObject implements Person {
    private final ConcurrentHashMap<String, RemoteAccount> accounts;
    private final String name;
    private final String surname;
    private final String passport;

    public RemotePerson(String name, String surname, String passport) throws RemoteException {
        super();
        this.name = name;
        this.surname = surname;
        this.passport = passport;
        this.accounts = new ConcurrentHashMap<>();
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public String getPassport() {
        return passport;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Account getAccount(String accountName) {
        return accounts.get(passport + ':' + accountName);
    }

    @Override
    public void setAccount(RemoteAccount account) {
        accounts.putIfAbsent(account.getId(), account);
    }

    LocalPerson getLocalPerson() {
        return new LocalPerson(name, surname, passport, accounts.values().stream());
    }
}
