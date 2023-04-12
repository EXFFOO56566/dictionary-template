package com.dictionary.codebhak.data.appdata;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * A contract class for the AppData database. 
 */
public final class AppDataContract {

    /**
     * Empty private constructor to prevent instantiation.
     */
    private AppDataContract() {}

    /**
     * A contract class for the wordbook history table.
     */
    public static abstract class WordbookHistory implements BaseColumns {
        public static final String TABLE_NAME = "wordbook_history";
        public static final String COLUMN_NAME_WORDBOOK_ID = "wordbookID";
        public static final String COLUMN_NAME_WORD = "word";
        public static final Uri CONTENT_URI = WordbookHistoryProvider.CONTENT_URI;
    }

    /**
     * A contract class for the wordbook favorites table.
     */
    public static abstract class WordbookFavorites implements BaseColumns {
        public static final String TABLE_NAME = "wordbook_favorites";
        public static final String COLUMN_NAME_WORDBOOK_ID = "wordbookID";
        public static final String COLUMN_NAME_WORD = "word";
        public static final Uri CONTENT_URI = WordbookFavoritesProvider.CONTENT_URI;
    }

    /**
     * A contract class for the subdict bookmarks table.
     */
    public static abstract class SubdictBookmarks implements BaseColumns {
        public static final String TABLE_NAME = "subdict_bookmarks";
        public static final String COLUMN_NAME_SYNTAX_ID = "subdict_id";
        public static final String COLUMN_NAME_SYNTAX_SECTION = "subdict_section";
        public static final Uri CONTENT_URI = SubdictBookmarksProvider.CONTENT_URI;
    }

    /**
     * A contract class for the subdict bookmarks table.
     */
    public static abstract class Category implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_NAME_CAT_NAME = "name";
    }
}
