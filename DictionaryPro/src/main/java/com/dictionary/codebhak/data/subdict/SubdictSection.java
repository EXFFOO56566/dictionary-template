package com.dictionary.codebhak.data.subdict;

import java.util.LinkedList;

/**
 * A class to hold data for a section of the Overview of Lang Subdict text.
 * <p>
 * Each section contains a heading and some intro text followed by a series of list items.
 */
public class SubdictSection {

    private String mHeading = "heading";
    private String mIntro = "intro";
    private final LinkedList<String> mList; // Represents bulleted list of items.

    /* NOTE: I'm leaving the construction of the string with formatting information to the XML
     * parser here. This is easier, but it would be cleaner to do all of the text construction
     * here. On the other hand, the parser and this class are so closely coupled that this might
     * not be a problem.
     */

    /**
     * Class constructor.
     */
    public SubdictSection() {
        mList = new LinkedList<>();
    }

    /**
     * Returns a string containing HTML formatting tags.
     *
     * @return a string containing HTML formatting tags
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<em>");
        buffer.append(mHeading);
        buffer.append("</em><br><br>");
        buffer.append(mIntro);

        /* TODO: Improve list. Subsequent paragraphs don't align at the moment.
         * May need to use spans to do this.
         */
        for (String item : mList) {
            buffer.append("&#8226 "); // Add a bullet
            buffer.append(item);
        }

        return buffer.toString();
    }

    public void setHeading(String heading) {
        this.mHeading = heading;
    }

    public void setIntro(String intro) {
        this.mIntro = intro;
    }

    public void addListItem(String item) {
        mList.add(item);
    }

    public String getHeading() {
        return mHeading;
    }
}
