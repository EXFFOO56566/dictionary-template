package com.dictionary.codebhak.data.subdict;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * A contract class for the subdict database.
 */
public final class SubdictContract implements BaseColumns {
    // TODO: Move content URI to this class.
    
    public static final Uri CONTENT_URI = SubdictProvider.CONTENT_URI;
    public static final String DB_NAME = "subdict";
    public static final String TABLE_NAME = "subdict";
    public static final String COLUMN_NAME_CHAPTER = "chapter";
    public static final String COLUMN_NAME_SECTION = "section";
    public static final String COLUMN_NAME_XML = "xml";

    /**
     * Empty private construtor to prevent instantiation.
     */
    private SubdictContract() {}
}
