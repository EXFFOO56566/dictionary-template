package com.dictionary.codebhak.data.wordbook;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * An {@link SQLiteAssetHelper} for the wordbook database.
 */
public class WordbookHelper extends SQLiteAssetHelper {
    private static final int DB_VERSION = 3;

    /**
     * Class constructor.
     * @param context the {@link Context} to use
     */
    public WordbookHelper(Context context) {
        super(context, WordbookContract.DB_NAME, null, DB_VERSION);
        setForcedUpgrade(DB_VERSION);
    }
}
