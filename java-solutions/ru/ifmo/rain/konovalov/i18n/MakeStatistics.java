package ru.ifmo.rain.konovalov.i18n;

import java.text.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Class for creating {@link Statistics} for a given text.
 *
 * @author Eugene Geny200
 */
public class MakeStatistics {
    private final Collator ruleCompare;
    private final Locale locale;
    private final Locale outPut;
    private final String data;

    /**
     * Constructs a new MakeStatistics class.
     *
     * @param data   {@link String} - given text.
     * @param locale {@link Locale} - input locale.
     * @param outPut {@link Locale} - output locale.
     */
    public MakeStatistics(String data, Locale locale, Locale outPut) {
        this.locale = locale;
        this.ruleCompare = Collator.getInstance(locale);
        this.data = data;
        this.outPut = outPut;
    }

    private Date testDate(String str, ParsePosition position) {
        for (Integer format : List.of(DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT)) {
            Date date = DateFormat.getDateInstance(format, locale).parse(str, position);
            if (date != null)
                return date;
        }
        return null;
    }

    private Long testCurrency(String str, ParsePosition position) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        Number result = currencyFormat.parse(str, position);
        if (result != null)
            return result.longValue();
        return null;
    }

    private Long testNumber(String str, ParsePosition position) {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance(locale);
        numberFormat.setGroupingUsed(true);
        numberFormat.setParseIntegerOnly(true);
        Number result = numberFormat.parse(str, position);
        if (result != null)
            return result.longValue();
        return null;
    }

    static private boolean checkEmpty(String str) {
        return str.isEmpty() || str.replaceAll("[\\W]", "").isEmpty();
    }

    private Statistics<String> getWordsSentencesLines(BreakIterator breakIterator, Function<String, Boolean> check) {
        BreakStatistics<String> breakStatistics = new BreakStatistics<>(breakIterator);
        return breakStatistics.getStatistic(
                ruleCompare,
                check,
                (s, parsePosition) -> {
                    String str = s.substring(parsePosition.getIndex(), parsePosition.getErrorIndex());
                    parsePosition.setIndex(parsePosition.getErrorIndex());
                    return str;
                },
                s -> s);
    }

    /**
     * Returns statistics of words for a given text.
     *
     * @return {@link Statistics} - statistics of words for a given text.
     */
    public Statistics<String> getWords() {
        return getWordsSentencesLines(BreakIterator.getWordInstance(locale), MakeStatistics::checkEmpty);
    }

    /**
     * Returns statistics of sentences for a given text.
     *
     * @return {@link Statistics} - statistics of sentences for a given text.
     */
    public Statistics<String> getSentences() {
        return getWordsSentencesLines(BreakIterator.getSentenceInstance(locale), MakeStatistics::checkEmpty);
    }

    /**
     * Returns statistics of lines for a given text.
     *
     * @return {@link Statistics} - statistics of lines for a given text.
     */
    public Statistics<String> getLines() {
        return getWordsSentencesLines(BreakIterator.getLineInstance(locale), String::isEmpty);
    }

    /**
     * Returns statistics of numbers for a given text.
     *
     * @return {@link Statistics} - statistics of numbers for a given text.
     */
    public Statistics<Long> getNumbers() {
        BreakStatistics<Long> breakStatistics = new BreakStatistics<>(BreakIterator.getWordInstance(locale));
        return breakStatistics.getStatistic(
                Comparator.naturalOrder(),
                MakeStatistics::checkEmpty,
                this::testNumber,
                number -> {
                    if (number != null)
                        return NumberFormat.getNumberInstance(outPut).format(number);
                    return "";
                });
    }

    /**
     * Returns statistics of dates for a given text.
     *
     * @return {@link Statistics} - statistics of dates for a given text.
     */
    public Statistics<Date> getDate() {
        BreakStatistics<Date> breakStatistics = new BreakStatistics<>(BreakIterator.getWordInstance(locale));
        return breakStatistics.getStatistic(
                Comparator.naturalOrder(),
                MakeStatistics::checkEmpty,
                this::testDate,
                date -> {
                    if (date != null)
                        return DateFormat.getDateInstance(DateFormat.DEFAULT, outPut).format(date);
                    return "";
                });
    }

    /**
     * Returns currency statistics for a given text.
     *
     * @return {@link Statistics} - currency statistics for a given text.
     */
    public Statistics<Long> getCurrency() {
        BreakStatistics<Long> breakStatistics = new BreakStatistics<>(BreakIterator.getWordInstance(locale));
        return breakStatistics.getStatistic(
                Comparator.naturalOrder(),
                MakeStatistics::checkEmpty,
                this::testCurrency,
                number -> {
                    if (number != null)
                        return NumberFormat.getCurrencyInstance(locale).format(number);
                    return "";
                });
    }

    private class BreakStatistics<T> {
        private final BreakIterator iterator;

        BreakStatistics(BreakIterator breakIterator) {
            this.iterator = breakIterator;
            iterator.setText(data);
        }

        Statistics<T> getStatistic(Comparator comparator,
                                   Function<String, Boolean> check,
                                   BiFunction<String, ParsePosition, T> fromString,
                                   Function<T, String> toString) {
            TreeSet<T> treeSet = new TreeSet<>(comparator);
            Statistics<T> wordStatistics = makeStatistics((parsePosition, statistics) -> {
                int begin = parsePosition.getIndex();
                T element = fromString.apply(data, parsePosition);
                String word = data.substring(begin, parsePosition.getIndex());
                if (check.apply(word))
                    return;
                statistics.increment(word, treeSet.add(element));
            }, toString);
            if (!treeSet.isEmpty()) {
                wordStatistics.setMinimum(treeSet.first());
                wordStatistics.setMaximum(treeSet.last());
            }
            return wordStatistics;
        }

        Statistics<T> makeStatistics(BiConsumer<ParsePosition, Statistics<T>> breakIteratorConsumer, Function<T, String> function) {
            Statistics<T> statistics = new Statistics<>(function);
            ParsePosition parsePosition = new ParsePosition(0);
            parsePosition.setErrorIndex(0);
            while (iterator.next() != BreakIterator.DONE) {
                if (parsePosition.getIndex() <= iterator.current()) {
                    parsePosition.setErrorIndex(iterator.current());
                    breakIteratorConsumer.accept(parsePosition, statistics);
                    if (parsePosition.getIndex() <= iterator.current())
                        parsePosition.setIndex(iterator.current());
                }
            }
            return statistics;
        }
    }
}
