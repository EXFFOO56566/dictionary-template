package com.dictionary.codebhak.data.appdata;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * A ContentProvider for the wordbook history table.
 */
public class WordbookHistoryProvider extends ContentProvider {

    public static final String AUTHORITY =
            "com.dictionary.codebhak.data.appdata.WordbookHistoryProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/appData");
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.codebhak.dictionarypro";
    public static final String CONTENT_WORD_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "vnd.codebhak.dictionarypro";
    public static final String LIMIT = "50"; // Limit on number of results returned by query

    private static final int WORDS = 0;
    private static final int WORD_ID = 1;
    private static final UriMatcher sMatcher = buildUriMatcher();

    private SQLiteDatabase mDatabase;

    /**
     * Returns a {@code UriMatcher} for this {@code ContentProvider}.
     * @return a {@code UriMatcher} for this {@code ContentProvider}.
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, "appData", WORDS);
        matcher.addURI(AUTHORITY, "appData/#", WORD_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        AppDataDbHelper dbHelper = new AppDataDbHelper(getContext());
        mDatabase = dbHelper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        switch (sMatcher.match(uri)) {
        case WORDS:
            return searchWords(uri, projection, selection, selectionArgs, sortOrder);
        case WORD_ID:
            return getWord(uri);
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    /**
     * Queries the database for words matching the specified criteria.
     * @return a {@code Cursor} containing the results of the query
     */
    private Cursor searchWords(Uri uri, String[] projection, String selection, String[] selectionArgs,
                    String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(AppDataContract.WordbookHistory.TABLE_NAME);
        Cursor cursor = queryBuilder.query(mDatabase, projection, selection, selectionArgs, null,
                null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Searches the database for the word specified by the given URI.
     * @param uri the {@code Uri} specifying the word for which to search
     * @return a {@code Cursor} containing the results of the query
     */
    private Cursor getWord(Uri uri) {
        String id = uri.getLastPathSegment();
        String[] projection = new String[] {BaseColumns._ID,
                AppDataContract.WordbookHistory.COLUMN_NAME_WORDBOOK_ID,
                AppDataContract.WordbookHistory.COLUMN_NAME_WORD};
        String selection = AppDataContract.WordbookHistory.COLUMN_NAME_WORDBOOK_ID + " = ?";
        String[] selectionArgs = new String[] {id};
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(AppDataContract.WordbookHistory.TABLE_NAME);
        Cursor cursor = queryBuilder.query(mDatabase, projection, selection, selectionArgs, null,
                null, null);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sMatcher.match(uri)) {
        case WORDS:
            return CONTENT_TYPE;
        case WORD_ID:
            return CONTENT_WORD_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = mDatabase.insert(AppDataContract.WordbookHistory.TABLE_NAME,
                AppDataContract.WordbookHistory.COLUMN_NAME_WORDBOOK_ID, values);
        Uri resultUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = sMatcher.match(uri);
        int affected;

        switch (match) {
        case WORDS:
            affected = mDatabase.delete(AppDataContract.WordbookHistory.TABLE_NAME,
                    selection, selectionArgs);
            break;
        case WORD_ID:
            long id = ContentUris.parseId(uri);
            affected = mDatabase.delete(AppDataContract.WordbookHistory.TABLE_NAME,
                    BaseColumns._ID + "=" + id + " AND (" + selection + ")",
                    selectionArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " +  uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return affected;
    }
}
