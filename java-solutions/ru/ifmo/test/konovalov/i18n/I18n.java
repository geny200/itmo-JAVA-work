package ru.ifmo.test.konovalov.i18n;

import java.util.Locale;

/**
 * @author Eugene Geny200
 */
public interface I18n {
    /**
     * Creates html text statistics for {@param inPutData}.
     *
     * @param localeInPut  - input locale.
     * @param localeOutPut - output locale.
     * @param inPutData    - text for analysis.
     * @return - statistics in html format.
     */
    String make(Locale localeInPut, Locale localeOutPut, String inPutData);
}
