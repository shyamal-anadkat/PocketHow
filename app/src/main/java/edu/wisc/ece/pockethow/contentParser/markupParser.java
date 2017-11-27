package edu.wisc.ece.pockethow.contentParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class markupParser {

    public markupParser() {
    }

    /**
     * Get Jsoup document from markup
     *
     * @param html
     * @return
     */
    public Document getDocFromString(String html) {
        return Jsoup.parse(html);
    }


    /**
     * Parse the content of the given PHArticle to prettify it up
     *
     * @param content
     * @return
     */
    @Deprecated
    public String stringCleaner(String content) {
        for (int i = 0; i < content.length(); i++) {
            //get rid of stub date
            //ex: in article "Check in at the Royal National Hotel

            //delete stub dates
            //TODO
            //ex: "buy a scale" {{stub|date=2016-08-18}}
            //{{Stub|date=2014-04-12}}
            boolean foundStub = false;
            do {


                if ((i + 1 < content.length()) && content.charAt(i) == '{' && content.charAt(i + 1) == '{') {
                    foundStub = true;
                    int numUnmatchedBrackets = 2;
                    int j = i + 1;
                    while (numUnmatchedBrackets > 0 && j < content.length()) {
                        if (content.charAt(j) == '}') {
                            numUnmatchedBrackets--;
                        }
                        j++;
                    }

                    String string1 = content.substring(0, i);
                    String string2 = content.substring(j);
                    content = string1 + string2;

                } else {
                    foundStub = false;
                }
            } while (foundStub);
            //delete ref tags
            //TODO: NEEDS WORK, some tags are still being written
            //check the "being a drifter" page
            boolean refTagFound = false;
            do {
                if ((i + "<ref>".length() < content.length()) && content.substring(i, i + "<ref>".length()).equals("<ref>")) {
                    int j = i + "<ref>".length();
                    refTagFound = true;
                /*
                while ((j+"</ref>".length() < content.length()) &&  !content.substring(j, j+"</ref>".length()).equals("</ref>") )
                {
                    j++;
                }
                */
                    char char1 = content.charAt(j);
                    while (j < content.length() && content.charAt(j) != '>') {
                        j++;
                        char1 = content.charAt(j);
                    }
                /*
                if(content.substring(j, j+"</ref>".length()).equals("</ref>"))
                {
                    String string1 = content.substring(0, i);
                    String string2 = content.substring(j+"</ref>".length());
                    content = string1 + string2;
                }
                */
                    if (content.charAt(j) == '>') {
                        j++;
                        String string1 = content.substring(0, i);
                        String string2 = content.substring(j);
                        content = string1 + string2;
                    }
                } else {
                    refTagFound = false;
                }
            } while (refTagFound);

            /*
            //delete everything that is in [[trash]]
            int leftBracketNum = 0;
            //int rightBracketNum = 0;
            if(content.charAt(i) == '[')
            {
                leftBracketNum++;
                int j = i+1;
                while(leftBracketNum > 0)
                {
                    if(content.charAt(j) == '[')
                    {
                        leftBracketNum++;
                    }
                    else if(content.charAt(j) == ']')
                    {
                        leftBracketNum--;
                    }
                    j++;
                }
                String string1 = content.substring(0, i);
                String string2 = content.substring(j);
                string1 += "\n";
                content = string1 + string2;
            }
            */
            //{{reflist}}

            //get ride of [[Image: ...]]
            //for example: [[Image:Convince People You Are a local step1.jpg|center]]
            boolean imageTagFound = false;
            do {


                if (i < content.length() && content.charAt(i) == '[' && i + 2 + ("Image".length()) < content.length() && content.charAt(i + 1) == '[') {
                    imageTagFound = true;
                    String s1 = content.substring(i + 2, i + 2 + ("Image".length()));
                    if (s1.equals("Image") || s1.equals("image") || s1.equals("Categ")) {
                        int j = i + 2 + ("Image".length());
                        //find the first ']'
                        while (j < content.length() && content.charAt(j) != ']') {
                            j++;
                        }
                        j = j + 2;
                        //Log.d("Editing", "string = " + content.substring(i,j));
                        String firstPart = content.substring(0, i);
                        String secondPart = content.substring(j);
                        content = firstPart.concat(secondPart);
                    } else {
                        imageTagFound = false;
                    }
                } else {
                    imageTagFound = false;
                }
            } while (imageTagFound);
            //get rid of more stuff
            //more parsing

        }
        return content;
    }

}
