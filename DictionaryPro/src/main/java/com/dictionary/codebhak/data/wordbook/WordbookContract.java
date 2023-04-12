package com.dictionary.codebhak.data.wordbook;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * A contract class for the wordbook database.
 */
public final class WordbookContract implements BaseColumns {
    // TODO: Move content URI to this class.
    
    public static final String DB_NAME = "wordbook";
    public static final String TABLE_NAME = "wordbook";
    public static final String COLUMN_ENTRY = "entry";
    public static final String COLUMN_LANG_NO_SYMBOLS = "langNoSymbols";
    public static final String COLUMN_LANG_LOWERCASE = "langLowercase";
    public static final String COLUMN_BETA_SYMBOLS = "betaSymbols";
    public static final String COLUMN_BETA_NO_SYMBOLS = "betaNoSymbols";
    public static final String COLUMN_LANG_FULL_WORD = "langFullWord";
    public static final Uri CONTENT_URI = WordbookProvider.CONTENT_URI;
    public static final String COLUMN_SOUND = "soundName";

    /**
     * Empty private constructor to prevent instantiation.
     */
    private WordbookContract() {}
}
