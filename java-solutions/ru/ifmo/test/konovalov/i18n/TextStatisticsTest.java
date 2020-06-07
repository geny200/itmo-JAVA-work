package ru.ifmo.test.konovalov.i18n;

import info.kgeorgiy.java.advanced.base.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import ru.ifmo.rain.konovalov.i18n.TextStatistics;
import ru.ifmo.test.common.bank.Account;
import ru.ifmo.test.common.bank.Bank;
import ru.ifmo.test.common.bank.BankClient;
import ru.ifmo.test.common.bank.Person;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Locale;

/**
 * Tests for the {@link ru.ifmo.rain.konovalov.i18n.TextStatistics}.
 *
 * @author Eugene Geny200
 * @see BaseTest
 */
public class TextStatisticsTest extends BaseTest {

    @Test
    public void testUSDate() {
        test(Locale.US, Locale.ENGLISH, "06.08.2020", "*(1 \\(1 unique\\))*(10 \\(*06.08.2020*\\))*(10 \\(*06.08.2020*\\))*(10)*");
    }

    @Test
    public void testUSNumber() {
        test(Locale.US, Locale.ENGLISH, "100", "*(1 \\(1 unique\\))*(100)*(100)*(3 \\(100\\))*(3 \\(100\\))*(3)*");
    }

    @Test
    public void testUSNumberAndCurrencyOne() {
        test(Locale.US, Locale.ENGLISH, "300 $100", "*(2 \\(2 unique\\))*(100)*(300)*(3 \\(*00\\))*(3 \\(*00\\))*(3)*(1 \\(1 unique\\))*(100)*(100)*(4 \\(*100\\))*(4 \\(*100\\))*(4)*");
    }

    @Test
    public void testUSCurrencyOne() {
        test(Locale.US, Locale.ENGLISH, "$100", "*(1 \\(1 unique\\))*(100)*(100)*(4 \\(*100\\))*(4 \\(*100\\))*(4)*");
    }

    @Test
    public void testUSCurrencyTwo() {
        test(Locale.US, Locale.ENGLISH, "$100 $200", "*(2 \\(2 unique\\))*(100)*(200)*(4 \\(*00\\))*(4 \\(*00\\))*(4)*");
    }

    @Test
    public void testUSCurrencyTwoSame() {
        test(Locale.US, Locale.ENGLISH, "$100,000 $100,000", "*(2 \\(1 unique\\))*(100,000)*(100,000)*(8 \\(*100,000\\))*(8 \\(*100,000\\))*(8)*");
    }

    @Test
    public void testUSCurrencyTwoLength() {
        test(Locale.US, Locale.ENGLISH, "$1000 $200", "*(2 \\(2 unique\\))*(200)*(1000)*(4 \\(*200\\))*(5 \\(*1000\\))*");
    }

    private static void test(Locale localeInPut, Locale localeOutPut, String inPutData, String outPutData) {
        I18n textStatistics = createCUT();
        try {
            String result = textStatistics.make(localeInPut, localeOutPut, inPutData);
            Assert.assertTrue(
                    "Expected: " + outPutData.replaceAll("\\*", "(\\\\s*\\\\S*.*)*") + ",\nBut was: " + result,
                    result.matches(outPutData.replaceAll("\\*", "(\\\\s*\\\\S*.*)*"))
            );
        } catch (Exception e) {
            Assert.fail("Exception: " + e.getMessage());
        }
    }
}
