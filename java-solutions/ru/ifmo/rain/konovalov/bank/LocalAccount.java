package ru.ifmo.rain.konovalov.bank;

import ru.ifmo.test.common.bank.Account;

import java.io.Serializable;

/**
 * @author Eugene Geny200
 * @see ru.ifmo.test.common.bank.Person
 */
class LocalAccount implements Account, Serializable {
    private final String id;
    private int amount;

    /**
     * Construct a new LocalAccount.
     */
    public LocalAccount(String id) {
        this.id = id;
    }

    /**
     * @return {@link String} - account identifier
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @return - money in the account
     */
    @Override
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount - new amount of money in the account
     */
    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }
}
