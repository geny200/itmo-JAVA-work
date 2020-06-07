package ru.ifmo.test.konovalov.i18n;

import info.kgeorgiy.java.advanced.base.BaseTester;

/**
 * Runs tests for the TextStatistics.
 *
 * @author Eugene Geny200
 */
public class Tester extends BaseTester {
    /**
     * Starts testing.
     * Use to start:
     * <ul>
     *         <li> {@code java <class_name> i18n <canonical_class_name>}
     *         calls {@link TextStatisticsTest}
     *         </li>
     * </ul>
     *
     * @param args array of input parameters ({@link String}).
     * @see I18n
     */
    public static void main(final String... args) {
        new Tester().add("i18n", TextStatisticsTest.class).run(args);
    }
}
