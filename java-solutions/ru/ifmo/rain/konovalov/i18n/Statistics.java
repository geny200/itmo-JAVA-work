package ru.ifmo.rain.konovalov.i18n;

import java.util.function.Function;

/**
 * Class for aggregating statistics.
 *
 * @author Eugene Geny200
 */
public class Statistics<T> {
    private T minimum;
    private T maximum;
    private String maximumLength;
    private String minimumLength;
    private long summLength;
    private long counter;
    private long counterUniq;
    private final Function<T, String> getString;

    /**
     * Constructs a new Statistics class.
     *
     * @param getString {@link Function} - function to convert type T to string.
     */
    Statistics(Function<T, String> getString) {
        this.getString = getString;
        this.minimum = null;
        this.maximum = null;
        this.maximumLength = "";
        this.minimumLength = "";
        this.summLength = 0;
        this.counter = 0;
        this.counterUniq = 0;
    }

    /**
     * @return - amount of elements.
     */
    public long getCounter() {
        return counter;
    }

    /**
     * @return - amount of unique elements.
     */
    public long getCounterUniq() {
        return counterUniq;
    }

    /**
     * @return - average length of elements.
     */
    public double getAvgLength() {
        if (counter == 0)
            return 0;
        return (double) summLength / counter;
    }

    /**
     * @return {@link String} - element with maximum length.
     */
    public String getMaximumLength() {
        return maximumLength;
    }

    /**
     * @return {@link String} - maximum element.
     */
    public String getMaximum() {
        return getString.apply(maximum);
    }

    /**
     * @return {@link String} - minimum element.
     */
    public String getMinimum() {
        return getString.apply(minimum);
    }

    /**
     * @return {@link String} - element with minimum length.
     */
    public String getMinimumLength() {
        return minimumLength;
    }

    /**
     * Increments the current state of the counters.
     *
     * @param str  {@link String} - string representation of the element.
     * @param uniq - true if the item is unique.
     */
    public void increment(String str, boolean uniq) {
        ++counter;
        if (uniq)
            ++counterUniq;
        summLength += str.length();
        if (str.length() > maximumLength.length())
            maximumLength = str;
        if (str.length() < minimumLength.length() || minimumLength.isEmpty())
            minimumLength = str;
    }

    /**
     * Sets the maximum element.
     *
     * @param maximum - maximum element.
     */
    public void setMaximum(T maximum) {
        this.maximum = maximum;
    }

    /**
     * Sets the minimum element.
     *
     * @param minimum - minimum element.
     */
    public void setMinimum(T minimum) {
        this.minimum = minimum;
    }
}
