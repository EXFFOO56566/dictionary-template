package com.dictionary.codebhak.data.appdata;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by ThangTB on 09/02/2015.
 */
public class CategoryProvider extends ContentProvider {
    private SQLiteDatabase mDatabase;

    @Override
    public boolean onCreate() {
        AppDataDbHelper dbHelper = new AppDataDbHelper(getContext());
        mDatabase = dbHelper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return getCat();
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Searches the database for the word specified by the given URI.
     * @return a {@code Cursor} containing the results of the query
     */
    private Cursor getCat() {
        String[] projection = new String[] {AppDataContract.Category.COLUMN_NAME_CAT_NAME};
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(AppDataContract.Category.TABLE_NAME);
        return queryBuilder.query(mDatabase, projection, null, null, null,null, null);
    }
}
