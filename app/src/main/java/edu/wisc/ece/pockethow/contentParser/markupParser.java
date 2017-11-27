package edu.wisc.ece.pockethow.contentParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class markupParser {

    public markupParser() {
    }

    /**
     * Get Jsoup document from markup
     * @param html
     * @return
     */
    public Document getDocFromString(String html) {
        return Jsoup.parse(html);
    }

}
