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
 * A ContentProvider for the subdict bookmarks table.
 */
public class SubdictBookmarksProvider extends ContentProvider {

    public static final String AUTHORITY
            = "com.dictionary.codebhak.data.appdata.SubdictBookmarksProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/appData");
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.codebhak.dictionarypro";
    public static final String CONTENT_WORD_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "vnd.codebhak.dictionarypro";

    private static final int SECTIONS = 0;
    private static final int SECTION_ID = 1;
    private static final UriMatcher sMatcher = buildUriMatcher();

    private SQLiteDatabase mDatabase;

    /**
     * Returns a {@code UriMatcher} for this {@code ContentProvider}.
     * @return a {@code UriMatcher} for this {@code ContentProvider}.
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, "appData", SECTIONS);
        matcher.addURI(AUTHORITY, "appData/#", SECTION_ID);

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
        case SECTIONS:
            return searchSections(uri, projection, selection, selectionArgs, sortOrder);
        case SECTION_ID:
            return getSection(uri);
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    /**
     * Queries the database for sections matching the specified criteria.
     * @return a {@code Cursor} containing the results of the query
     */
    private Cursor searchSections(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(AppDataContract.SubdictBookmarks.TABLE_NAME);
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
    private Cursor getSection(Uri uri) {
        String id = uri.getLastPathSegment();
        String[] projection = new String[] {BaseColumns._ID,
                AppDataContract.SubdictBookmarks.COLUMN_NAME_SYNTAX_ID,
                AppDataContract.SubdictBookmarks.COLUMN_NAME_SYNTAX_SECTION};
        String selection = AppDataContract.SubdictBookmarks.COLUMN_NAME_SYNTAX_ID + " = ?";
        String[] selectionArgs = new String[] {id};
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(AppDataContract.SubdictBookmarks.TABLE_NAME);

        return queryBuilder.query(mDatabase, projection, selection, selectionArgs, null,
                null, null);
    }

    @Override
    public String getType(Uri uri) {
        switch (sMatcher.match(uri)) {
        case SECTIONS:
            return CONTENT_TYPE;
        case SECTION_ID:
            return CONTENT_WORD_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = mDatabase.insert(AppDataContract.SubdictBookmarks.TABLE_NAME,
                AppDataContract.SubdictBookmarks.COLUMN_NAME_SYNTAX_ID, values);
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
        case SECTIONS:
            affected = mDatabase.delete(AppDataContract.SubdictBookmarks.TABLE_NAME,
                    selection, selectionArgs);
            break;
        case SECTION_ID:
            long id = ContentUris.parseId(uri);
            affected = mDatabase.delete(AppDataContract.SubdictBookmarks.TABLE_NAME,
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
