package com.dictionary.codebhak.data.wordbook;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.LeadingMarginSpan;
import android.text.style.StyleSpan;

import java.util.ArrayList;

/**
 * A class representing a wordbook entry.
 */
public class WordbookEntry {

    // Names are identical to XML element names.
    // Comments indicate structure within "entry" element.
    private String mWord = null; // entry element's "key" attribute, converted to Lang text
    private String mOrth = null; // form -> mOrth; Is this redundant with key?
    private String mNote = null; // top-level
    private String mRef = null; // etym->xr->mRef
    private ArrayList<Sense> mSenseList = null; // top-level

    private String soundName = null;

    /**
     * Class constructor.
     */
    public WordbookEntry() {
        mSenseList = new ArrayList<>();
    }

    /**
     * Returns a formatted version of this wordbook entry contained in a {@code CharSequence}
     * with spans specifying style information.
     * @param textSize the size of the font in the {@code LangTextView} to which this
     *                 {@code CharSequence} will be added. Used to calculate indent size.
     * @return a {@code CharSequence} containing this wordbook entry
     */
    public SpannedString toSpanned(float textSize) {
        // TOOD: Split this into multiple methods.

        // Create heading string.
        // Use the "orth" value, not the "key" value!
        //SpannableString wordStr = new SpannableString(mWord);
        SpannableString wordStr = new SpannableString(mOrth);
        wordStr.setSpan(new StyleSpan(Typeface.BOLD), 0, wordStr.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Create mRef string.
        SpannableString refStr;
        if (mRef != null) {
            refStr = new SpannableString("\n\n" + mRef);
            refStr.setSpan(new StyleSpan(Typeface.ITALIC), 0, refStr.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            refStr = new SpannableString("");
        }

        // Create mNote string.
        SpannableString noteStr;
        if (mNote != null) {
            if (',' == mNote.charAt(mNote.length() - 1)) {
                mNote = mNote.substring(0, mNote.length() - 1);
            }
            noteStr = new SpannableString("\n\n" + mNote);
        } else {
            noteStr = new SpannableString("");
        }

        // Create sense list string.
        SpannedString senseListStr = new SpannedString(""); // N.B.: Spanned, not spannable.
        for (Sense sense : mSenseList) {
            // Add heading.
            String headingStr = "\n\n";
            int level = sense.getLevel();
            if (level > 0) {
                headingStr += sense.getN() + ". ";
            }

            // Make heading bold.
            SpannableString headingSpanStr = new SpannableString(headingStr);
            headingSpanStr.setSpan(new StyleSpan(Typeface.BOLD), 0, headingSpanStr.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Add definition text.
            CharSequence temp = TextUtils.concat(headingSpanStr, sense.getText());
            SpannableString senseStr = new SpannableString(temp);

            // Add indentation.
            if (level > 1) {
                int indent = (int) ((level - 1) * textSize);
                senseStr.setSpan(new LeadingMarginSpan.Standard(indent), 0, senseStr.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            // Concatenate with senseListStr.
            // TODO: Can I create some sort of buffer to speed this up?
            senseListStr = (SpannedString) TextUtils.concat(senseListStr, senseStr);
        }

        // Concatenate strings and return resulting CharSequence.
        return new SpannedString(TextUtils.concat(wordStr, refStr, senseListStr, noteStr));
    }

    public void setWord(String word) {
        this.mWord = word;
    }

    public void setOrth(String orth) {
        this.mOrth = orth;
    }

    public String getWord() {
        return mWord;
    }

    public String getOrth() {
        return mOrth;
    }

    /**
     * Adds a mNote to the mNote string. Note that this does not replace previously added notes.
     *
     * @param note The mNote to add.
     */
    public void addNote(String note) {
        if (null == this.mNote) {
            this.mNote = note;
        } else {
            this.mNote += "\n\n" + note; // Create a list of notes separated by blank lines.
        }
    }

    public void setRef(String ref) {
        this.mRef = ref;
    }

    /**
     * Adds a new {@code Sense} to this wordbook entry.
     * @param level the level of the entry hierarchy at which this sense item is found
     * @param n the symbol to display at the start of this sense item
     * @param text the text of this sense item
     */
    public void addSense(String level, String n, SpannableString text) {
        Sense sense = new Sense(level, n, text);
        mSenseList.add(sense);
    }

    /**
     * A class representing a "sense" element in a wordbook XML document.
     */
    private static class Sense {
        private final int level; // attribute -- numerical level
        private final String n; // attribute -- displayed level
        private final SpannedString text; // Full text, including formatting.

        /**
         * Constructs a new {@code Sense}.
         * @param level the level of the entry hierarchy at which this sense item is found
         * @param n the symbol to display at the start of this sense item
         */
        public Sense(String level, String n, SpannableString text) {
            this.level = Integer.parseInt(level);
            this.n = n;
            this.text = new SpannedString(text);
        }

        public int getLevel() {
            return level;
        }

        public String getN() {
            return n;
        }

        public SpannedString getText() {
            return text;
        }
    }
}
