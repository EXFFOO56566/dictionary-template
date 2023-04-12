package com.dictionary.codebhak.data.subdict;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * An {@link SQLiteAssetHelper} for the subdict database.
 */
public class SubdictHelper extends SQLiteAssetHelper {
    private static final int DB_VERSION = 1;

    /**
     * Class constructor.
     * @param context   the <code>Context</code> to use
     */
    public SubdictHelper(Context context) {
        super(context, SubdictContract.DB_NAME, null, DB_VERSION);
        setForcedUpgrade(DB_VERSION); // Copy entire database on upgrade.
    }
}
