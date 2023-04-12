package com.dictionary.codebhak.data.subdict;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * A {@link ContentProvider} for the subdict database.
 */
public class SubdictProvider extends ContentProvider {

    public static final String AUTHORITY = 
            "com.dictionary.codebhak.data.subdict.SubdictProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/subdict");
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.codebhak.dictionarypro";
    public static final String CONTENT_SECTION_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "vnd.codebhak.dictionarypro";

    private static final int SECTIONS = 0;
    private static final int SECTION_ID = 1;
    private static final UriMatcher sMatcher = buildUriMatcher();
    
    private SQLiteDatabase mDatabase = null;
    private SubdictHelper mHelper;
    
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, "subdict", SECTIONS);
        matcher.addURI(AUTHORITY, "subdict/#", SECTION_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mHelper = new SubdictHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        getReadableDatabase();

        switch (sMatcher.match(uri)) {
            case SECTIONS:
                return searchSections(uri, projection, selection, selectionArgs);
            case SECTION_ID:
                return getSection(uri);
            default:
                throw new IllegalArgumentException("Unkown URI: " + uri);
        }
    }

    /**
     * Searches the database.
     * @param uri               the <code>uri</code> used to conduct the query
     * @param projection        the columns to select
     * @param selection         the parameterized search criteria
     * @param selectionArgs     the search criteria arguments
     * @return a <code>Cursor</code> containing the results of the query
     */
    private Cursor searchSections(Uri uri, String[] projection, String selection,
                                  String[] selectionArgs) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(SubdictContract.TABLE_NAME);
        Cursor cursor = queryBuilder.query(mDatabase, projection, selection, selectionArgs, null,
                null, null);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Queries the database for the section specified by the given URI.
     * @param uri   a <code>Uri</code> specifying the section for which to search
     * @return a <code>Cursor</code> containing the results of the query
     */
    private Cursor getSection(Uri uri) {
        String id = uri.getLastPathSegment();
        String[] projection = new String[] {
            SubdictContract._ID,
            SubdictContract.COLUMN_NAME_CHAPTER,
            SubdictContract.COLUMN_NAME_SECTION,
            SubdictContract.COLUMN_NAME_XML
        };
        String selection = SubdictContract._ID + " = ?";
        String[] selectionArgs = new String[] {id};
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(SubdictContract.TABLE_NAME);
        Cursor cursor = queryBuilder.query(mDatabase, projection, selection, selectionArgs, null,
                null, null);
        assert cursor != null;
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sMatcher.match(uri)) {
            case SECTIONS:
                return CONTENT_TYPE;
            case SECTION_ID:
                return CONTENT_SECTION_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    // Only queries are supported.

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    public void getReadableDatabase() {
        if (mDatabase == null) {
            mDatabase = mHelper.getReadableDatabase();
        }
    }
}
