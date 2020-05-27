package ru.ifmo.rain.konovalov.bank;

import java.io.Serializable;

class LocalAccount implements Account, Serializable {
    private final String id;
    private int amount;

    public LocalAccount(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
