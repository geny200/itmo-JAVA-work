package ru.ifmo.rain.konovalov.i18n;

import java.util.ArrayList;

/**
 * Class for building an HTML document.
 *
 * @author Eugene Geny200
 */
public class HtmlDocument {
    static private final String TEMPLATE_HTML = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>I18n Geny200</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "%s\n" +
            "</body>\n" +
            "</html>";
    static private final String TEMPLATE_LI = "<li>%s</li>\n";
    static private final String TEMPLATE_LIST = "\n<ul>%s</ul>\n";
    private final StringBuilder document;

    /**
     * Enumerate languages for a tag in an HTML document.
     */
    public enum Lang {
        ru,
        en
    }

    /**
     * Constructs a new HtmlDocument.
     */
    public HtmlDocument(Lang lang) {
        this.document = new StringBuilder();
    }

    static private String getLi(String data) {
        return String.format(TEMPLATE_LI, data);
    }

    static private String getUl(String data) {
        return String.format(TEMPLATE_LIST, data);
    }

    static private String makeBold(String data) {
        return String.format("<b>%s</b>", data);
    }

    private String makeUl(String head, ArrayList<String> list) {
        head = makeBold(head);
        return getUl(head
                + getUl(list
                .stream()
                .map(HtmlDocument::getLi)
                .reduce("", String::concat)));
    }

    /**
     * Inserts an unordered list at the end.
     *
     * @param head {@link String} - list heading.
     * @param list {@link ArrayList<String>} - list items.
     */
    public void addUlEnd(String head, ArrayList<String> list) {
        document.append(makeUl(head, list));
    }

    /**
     * Inserts an unordered list at the beginning.
     *
     * @param head {@link String} - list heading.
     * @param list {@link ArrayList} - list items.
     */
    public void addUlBegin(String head, ArrayList<String> list) {
        document.insert(0, makeUl(head, list));
    }

    /**
     * Get formatted HTML document.
     *
     * @return {@link String} - returns a formatted HTML document.
     */
    public String getDocument() {
        return String.format(TEMPLATE_HTML, document);
    }
}
