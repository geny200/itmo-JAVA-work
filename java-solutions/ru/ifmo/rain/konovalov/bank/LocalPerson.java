package ru.ifmo.rain.konovalov.bank;

import ru.ifmo.test.common.bank.Account;
import ru.ifmo.test.common.bank.Person;

import java.io.Serializable;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * @author Eugene Geny200
 * @see ru.ifmo.test.common.bank.Person
 */
public class LocalPerson implements Person, Serializable {
    private final HashMap<String, LocalAccount> accounts;
    private final String name;
    private final String surname;
    private final String passport;

    /**
     * Construct a new LocalAccount.
     */
    public LocalPerson(String name, String surname, String passport, Stream<RemoteAccount> accountStream) {
        this.name = name;
        this.surname = surname;
        this.passport = passport;
        this.accounts = new HashMap<>();
        accountStream.map(RemoteAccount::getLocalAccount).forEach(localAccount -> accounts.put(localAccount.getId(), localAccount));
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
        LocalAccount localAccount = account.getLocalAccount();
        accounts.put(localAccount.getId(), localAccount);
    }
}
