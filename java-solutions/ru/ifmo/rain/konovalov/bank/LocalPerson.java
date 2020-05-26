package ru.ifmo.rain.konovalov.bank;

import ru.ifmo.rain.common.bank.Account;
import ru.ifmo.rain.common.bank.Person;

import java.io.Serializable;
import java.util.HashMap;
import java.util.stream.Stream;

public class LocalPerson implements Person, Serializable {
    private final HashMap<String, LocalAccount> accounts;
    private final String name;
    private final String surname;
    private final String passport;

    public LocalPerson(String name, String surname, String passport, Stream<RemoteAccount> accountStream) {
        this.name = name;
        this.surname = surname;
        this.passport = passport;
        this.accounts = new HashMap<>();
        accountStream.map(RemoteAccount::getLocalAccount).forEach(localAccount -> accounts.put(localAccount.getId(), localAccount));
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

    public void setAccount(RemoteAccount account) {
        LocalAccount localAccount = account.getLocalAccount();
        accounts.put(localAccount.getId(), localAccount);
    }
}
