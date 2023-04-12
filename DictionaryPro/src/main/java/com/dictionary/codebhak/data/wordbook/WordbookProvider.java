package com.dictionary.codebhak.data.wordbook;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;

/**
 * A content provider for the wordbook database.
 */
public class WordbookProvider extends ContentProvider {

    public static final String AUTHORITY = "com.dictionary.codebhak.data.wordbook.WordbookProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/wordbook");

    public static final String WORDS_MIME_TYPE = 
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.codebhak.dictionarypro";
    public static final String ENTRY_MIME_TYPE = 
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.codebhak.dictionarypro";

    private static final String LIMIT = "20"; // Maximum number of search suggestions to return
    private static final int SEARCH = 0;
    private static final int SEARCH_SUGGEST = 1;
    private static final int GET_WORD = 2;
    private static final UriMatcher sMatcher = buildUriMatcher();

    private WordbookHelper mHelper;
    private SQLiteDatabase mDatabase = null;
    
    /**
     * Returns a {@code UriMatcher} for this {@code ContentProvider}.
     * @return a {@code UriMatcher} for this {@code ContentProvider}.
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, "wordbook", SEARCH);
        matcher.addURI(AUTHORITY, "wordbook/#", GET_WORD);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mHelper = new WordbookHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        getReadableDatabase();

        switch (sMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                if (null == selectionArgs) {
                    throw new IllegalArgumentException(
                            "selectionArgs must be provided for the URI: " + uri);
                }
                return getSuggestions(selectionArgs[0]);
            case SEARCH:
                if (null == selectionArgs) {
                    throw new IllegalArgumentException(
                            "selectionArgs must be provided for the URI: " + uri);
                }
                return search(uri, projection, selection, selectionArgs, sortOrder);
            case GET_WORD:
                return getWord(uri);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    /**
     * Searches the database.
     * @param uri the {@code uri} used to conduct the query
     * @param projection the columns to select
     * @param selection the parameterized search criteria
     * @param selectionArgs the search criteria arguments
     * @param sortOrder the order in which to sort the results
     * @return a {@code Cursor} containing the results of the query
     */
    private Cursor search(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(WordbookContract.TABLE_NAME);
        Cursor cursor = queryBuilder.query(mDatabase, projection, selection, selectionArgs, null,
                null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Searches the database for suggestions matching the specified text.
     * @param query the search query for which to generate suggestions
     * @return a {@code Cursor} containing search suggestions
     */
    private Cursor getSuggestions(String query) {
        // TODO: Change "_ID" to "_id" in database schema.

        // We can only display polytonic characters in search suggestions on Android 5.0 and later.
        String suggestionCol;
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           suggestionCol = WordbookContract.COLUMN_LANG_FULL_WORD;
        } else {
           suggestionCol = WordbookContract.COLUMN_LANG_NO_SYMBOLS;
        }*/
        suggestionCol = WordbookContract.COLUMN_LANG_FULL_WORD;
        String[] projection = new String[] {
            "_id as " + WordbookContract._ID,
            suggestionCol + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1,
            WordbookContract.COLUMN_LANG_LOWERCASE,
            "_id AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
        };
        
        String selection = WordbookContract.COLUMN_BETA_SYMBOLS + " LIKE ? OR "
                + WordbookContract.COLUMN_BETA_NO_SYMBOLS + " LIKE ? OR "
				+ WordbookContract.COLUMN_LANG_FULL_WORD + " LIKE ? OR "
                + WordbookContract.COLUMN_LANG_LOWERCASE + " LIKE ?";

		
        String[] selectionArgs = new String[] {
            query.toLowerCase() + "%",
			query.toLowerCase() + "%",
			query + "%",
            query.toLowerCase() + "%"
        };
        
        String sortOrder = WordbookContract.COLUMN_LANG_FULL_WORD + " ASC";
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(WordbookContract.TABLE_NAME);
        return queryBuilder.query(mDatabase, projection, selection, selectionArgs, null, null,
                sortOrder, LIMIT);
    }

    /**
     * Queries the database for the word specified by the given URI.
     * @param uri a {@code Uri} specifying the word for which to search
     * @return a {@code Cursor} containing the results of the query
     */
    private Cursor getWord(Uri uri) {
        String id = uri.getLastPathSegment();
        String[] projection = new String[] {
                WordbookContract._ID,
                WordbookContract.COLUMN_ENTRY,
                WordbookContract.COLUMN_LANG_NO_SYMBOLS,
                WordbookContract.COLUMN_SOUND
        };
        String selection = "_id = ?";
        String[] selectionArgs = new String[] {id};
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(WordbookContract.TABLE_NAME);
        return queryBuilder.query(mDatabase, projection, selection, selectionArgs, null,
                null, null);
    }

    @Override
    public String getType(Uri uri) {
        switch (sMatcher.match(uri)) {
        case SEARCH:
            return WORDS_MIME_TYPE;
        case SEARCH_SUGGEST:
            return SearchManager.SUGGEST_MIME_TYPE;
        case GET_WORD:
            return ENTRY_MIME_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    private void getReadableDatabase() {
        if (mDatabase == null) {
            mDatabase = mHelper.getReadableDatabase();
        }
    }
}
