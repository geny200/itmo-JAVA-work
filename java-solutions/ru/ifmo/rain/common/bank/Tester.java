package ru.ifmo.rain.common.bank;

import info.kgeorgiy.java.advanced.base.BaseTester;

/**
 * Runs tests for the bank.
 *
 * @author Eugene Geny200
 */
public class Tester extends BaseTester {
    /**
     * Starts testing.
     * Use to start:
     * <ul>
     *         <li> {@code java <class_name> client <canonical_class_name>}
     *         calls {@link ru.ifmo.rain.common.bank.BankClientTest}
     *         </li>
     *         <li> {@code java <class_name> bank <canonical_class_name>}
     *         calls {@link ru.ifmo.rain.common.bank.BankServerTest}
     *         </li>
     * </ul>
     *
     * @param args array of input parameters ({@link java.lang.String}).
     * @see ru.ifmo.rain.common.bank.BankServerTest
     * @see ru.ifmo.rain.common.bank.BankClientTest
     * @see ru.ifmo.rain.common.bank.BankServer
     * @see ru.ifmo.rain.common.bank.BankClient
     */
    public static void main(final String... args) {
        new Tester().add("bank", BankServerTest.class).add("client", BankClientTest.class).run(args);
    }
}
