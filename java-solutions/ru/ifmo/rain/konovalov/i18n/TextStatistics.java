package ru.ifmo.rain.konovalov.i18n;

import ru.ifmo.test.konovalov.i18n.I18n;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.*;

/**
 * HW 13 TextStatistics. Tests for this class {@link ru.ifmo.test.konovalov.i18n.TextStatisticsTest}
 *
 * @author Eugene Geny200
 * @see ru.ifmo.test.konovalov.i18n.I18n
 */
public class TextStatistics implements I18n {
    /**
     * Starts TextStatistics.
     * Use to start:
     * <ul>
     *         <li> {@code java TextStatistics <input_locale> <output_locale> <input_file> <output_file>}</li>
     * </ul>
     *
     * @param args array of input parameters ({@link String}).
     * @see I18n
     */
    public static void main(String[] args) {
        if (args == null || args.length != 4) {
            System.out.println("Input arguments should be 4 : input Locale, output Locale, input File, output File");
            return;
        }
        for (int i = 0; i != 4; ++i) {
            if (args[i] == null) {
                System.out.println("Invalid input argument " + i + " - null argument)");
                return;
            }
        }

        try (BufferedReader is = Files.newBufferedReader(Paths.get(args[2]), StandardCharsets.UTF_8)) {
            try (BufferedWriter os = Files.newBufferedWriter(Paths.get(args[3] + ".html"), StandardCharsets.UTF_8)) {
                TextStatistics textStatistics = new TextStatistics();
                os.write(textStatistics.make(Locale.forLanguageTag(args[0]), Locale.forLanguageTag(args[1]), is.lines().reduce("", (s, s2) -> s + s2)));
            } catch (IOException e) {
                System.out.println("An output error occurred:" + e.getMessage());
            } catch (InvalidPathException e) {
                System.out.println("Invalid output path:" + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("An input error occurred:" + e.getMessage());
        } catch (InvalidPathException e) {
            System.out.println("Invalid input path:" + e.getMessage());
        }
    }

    /**
     * Creates html text statistics for {@param inPutData}.
     *
     * @param localeInPut  - input locale.
     * @param localeOutPut - output locale.
     * @param inPutData    - text for analysis.
     * @return - statistics in html format.
     */
    @Override
    public String make(Locale localeInPut, Locale localeOutPut, String inPutData) {
        ResourceBundle bundle = PropertyResourceBundle.getBundle("ru.ifmo.rain.konovalov.i18n.properties.TextStatistics", localeOutPut);
        MakeHtml makeHtml = new MakeHtml(localeOutPut, bundle);
        MakeStatistics makeStatistics = new MakeStatistics(inPutData, localeInPut, localeOutPut);

        makeHtml.setFromStatistics("line", makeStatistics.getLines());
        makeHtml.setFromStatistics("word", makeStatistics.getWords());
        makeHtml.setFromStatistics("sentence", makeStatistics.getSentences());
        makeHtml.setFromStatistics("number", makeStatistics.getNumbers());
        makeHtml.setFromStatistics("date", makeStatistics.getDate());
        makeHtml.setFromStatistics("currency", makeStatistics.getCurrency());
        makeHtml.setFromCache("common");

        return makeHtml.getHtmlDocument();
    }
}
