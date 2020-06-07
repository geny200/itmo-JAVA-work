package ru.ifmo.rain.konovalov.i18n;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Class for inserting formatted data into an {@link HtmlDocument}.
 *
 * @author Eugene Geny200
 */
public class MakeHtml {
    private final HtmlDocument htmlDocument;
    private final ArrayList<String> commonData;
    static private final String CNT = "cnt";
    static private final String MINIMUM = "minimum.data";
    static private final String MAXIMUM = "maximum.data";
    static private final String MINIMUM_LEN = "minimum.length";
    static private final String MAXIMUM_LEN = "maximum.length";
    static private final String AVG = "avg";
    private final Locale locale;
    private final ResourceBundle bundle;

    /**
     * Constructs a new MakeHtml class.
     *
     * @param locale - output locale.
     * @param bundle - internationalized resources.
     */
    public MakeHtml(Locale locale, ResourceBundle bundle) {
        this.commonData = new ArrayList<>();
        this.bundle = bundle;
        switch (locale.toLanguageTag()) {
            case "ru":
                this.htmlDocument = new HtmlDocument(HtmlDocument.Lang.ru);
                break;
            case "en":
            default:
                this.htmlDocument = new HtmlDocument(HtmlDocument.Lang.en);
        }
        this.locale = locale;
    }

    private class BundleFormat {
        private final String name;

        BundleFormat(String name) {
            this.name = name;
        }

        private String format(String pattern, Object... arguments) {
            MessageFormat messageFormat = new MessageFormat(pattern, locale);
            return messageFormat.format(arguments);
        }

        public String format(String key, long num, long uniq) {
            return format("{2}: {1,number} ({0,choice," + bundle.getString(name + ".uniq") + "})", uniq, num, bundle.getString(name + "." + key));
        }

        public String format(String key, long num, String word) {
            return format("{0}: {1,number} ({2})", bundle.getString(name + "." + key), num, word);
        }

        public String format(String key, String word) {
            return format("{0}: {1}", bundle.getString(name + "." + key), word);
        }

        public String format(String key, double number) {
            return format("{0}: {1, number}", bundle.getString(name + "." + key), number);
        }
    }

    /**
     * Inserts statistics into the document.
     *
     * @param name       {@link String}     - key for {@link ResourceBundle}.
     * @param statistics {@link Statistics} - statistics to insert.
     */
    public void setFromStatistics(String name, Statistics<?> statistics) {
        ArrayList<String> result = new ArrayList<>(6);
        BundleFormat bundleFormat = new BundleFormat(name);

        commonData.add(bundleFormat.format(CNT, statistics.getCounter()));
        result.add(bundleFormat.format(CNT, statistics.getCounter(), statistics.getCounterUniq()));
        result.add(bundleFormat.format(MINIMUM, statistics.getMinimum()));
        result.add(bundleFormat.format(MAXIMUM, statistics.getMaximum()));
        result.add(bundleFormat.format(MINIMUM_LEN, statistics.getMinimumLength().length(), statistics.getMinimumLength()));
        result.add(bundleFormat.format(MAXIMUM_LEN, statistics.getMaximumLength().length(), statistics.getMaximumLength()));
        result.add(bundleFormat.format(AVG, statistics.getAvgLength()));

        htmlDocument.addUlEnd(bundle.getString(name + ".name"), result);
    }

    /**
     * Inserts summary statistics from the cache into the document.
     *
     * @param name {@link String} - key for {@link ResourceBundle}.
     */
    public void setFromCache(String name) {
        htmlDocument.addUlBegin(bundle.getString(name + ".name"), commonData);
    }

    /**
     * Get formatted HTML document.
     *
     * @return {@link String} - returns a formatted HTML document
     */
    public String getHtmlDocument() {
        return htmlDocument.getDocument();
    }
}
