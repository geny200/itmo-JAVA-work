package ru.ifmo.rain.konovalov.bank;

import ru.ifmo.rain.common.bank.Account;
import ru.ifmo.rain.common.bank.Bank;
import ru.ifmo.rain.common.bank.Person;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

class RemoteBank implements Bank {
    private final ConcurrentHashMap<String, RemoteAccount> accounts;
    private final ConcurrentHashMap<String, RemotePerson> persons;

    RemoteBank() {
        this.accounts = new ConcurrentHashMap<>();
        this.persons = new ConcurrentHashMap<>();
    }

    @Override
    public Account createAccount(String id) throws RemoteException {
        RemoteAccount account = new RemoteAccount(id);
        accounts.put(id, account);
        return account;
    }

    @Override
    public Person createPerson(String name, String surname, String passport) throws RemoteException {
        RemotePerson remotePerson = new RemotePerson(name, surname, passport);
        RemotePerson person = persons.putIfAbsent(passport, remotePerson);
        if (person != null)
            return person;
        return remotePerson;
    }

    @Override
    public LocalPerson getLocalPerson(String passport) {
        RemotePerson person = persons.get(passport);
        if (person == null)
            return null;
        return person.getLocalPerson();
    }

    @Override
    public Person getRemotePerson(String passport) {
        return persons.get(passport);
    }

    @Override
    public Account getAccount(String id) {
        return accounts.get(id);
    }

    @Override
    public Account createPersonAccount(String accountName, Person person) throws RemoteException {
        if (person instanceof LocalPerson) {
            RemoteAccount account = new RemoteAccount(person.getPassport() + ':' + accountName);
            person.setAccount(account);
        }
        RemotePerson remotePerson = persons.get(person.getPassport());
        RemoteAccount account;

        String id = remotePerson.getPassport() + ':' + accountName;
        account = new RemoteAccount(id);
        RemoteAccount accountPresent = accounts.putIfAbsent(id, account);
        if (accountPresent != null)
            account = accountPresent;
        remotePerson.setAccount(account);

        return account;
    }
}
