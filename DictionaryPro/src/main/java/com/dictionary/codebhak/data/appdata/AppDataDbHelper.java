package com.dictionary.codebhak.data.appdata;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dictionary.codebhak.data.wordbook.WordbookContract;

/**
 * An {@link SQLiteOpenHelper} for the AppData database.
 */
public class AppDataDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "AppData.db";
    
    private final Context mContext;

    private static final String SQL_CREATE_WORDBOOK_HISTORY_TABLE = "CREATE TABLE "
            + AppDataContract.WordbookHistory.TABLE_NAME + " (" + AppDataContract.WordbookHistory._ID
            + " INTEGER PRIMARY KEY, " + AppDataContract.WordbookHistory.COLUMN_NAME_WORDBOOK_ID
            + " INTEGER, " + AppDataContract.WordbookHistory.COLUMN_NAME_WORD + " TEXT " + ")";

    private static final String SQL_CREATE_WORDBOOK_FAVORITES_TABLE = "CREATE TABLE "
            + AppDataContract.WordbookFavorites.TABLE_NAME + " (" 
            + AppDataContract.WordbookFavorites._ID + " INTEGER PRIMARY KEY, " 
            + AppDataContract.WordbookFavorites.COLUMN_NAME_WORDBOOK_ID + " INTEGER, " 
            + AppDataContract.WordbookFavorites.COLUMN_NAME_WORD + " TEXT" + ")";

    private static final String SQL_CREATE_SYNTAX_BOOKMARKS_TABLE = "CREATE TABLE "
            + AppDataContract.SubdictBookmarks.TABLE_NAME + " ("
            + AppDataContract.SubdictBookmarks._ID + " INTEGER PRIMARY KEY, "
            + AppDataContract.SubdictBookmarks.COLUMN_NAME_SYNTAX_ID + " INTEGER, "
            + AppDataContract.SubdictBookmarks.COLUMN_NAME_SYNTAX_SECTION + " TEXT " + ")";
    
    private static final String SQL_DELETE_WORDBOOK_FAVORITES_TABLE = "DROP TABLE IF EXISTS "
            + AppDataContract.WordbookFavorites.TABLE_NAME;

    /**
     * Class constructor.
     * @param context the {@code Context} to use
     */
    public AppDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_WORDBOOK_FAVORITES_TABLE);
        db.execSQL(SQL_CREATE_WORDBOOK_HISTORY_TABLE);
        db.execSQL(SQL_CREATE_SYNTAX_BOOKMARKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This method handles upgrades from versions 1 and 2 of the database to version 3.

        // Get a cursor containing the contents of the old Wordbook Favorites table.
        String table = AppDataContract.WordbookFavorites.TABLE_NAME;
        String[] columns = {AppDataContract.WordbookFavorites.COLUMN_NAME_WORD};
        Cursor oldData = db.query(table, columns, null, null, null, null, null, null);

        // Drop and recreate the Wordbook Favorites table.
        db.execSQL(SQL_DELETE_WORDBOOK_FAVORITES_TABLE);
        db.execSQL(SQL_CREATE_WORDBOOK_FAVORITES_TABLE);

        // Repopulate the table.
        ContentResolver resolver = mContext.getContentResolver();
        String[] projection = {WordbookContract._ID};
        String selection = WordbookContract.COLUMN_LANG_FULL_WORD + " = ?";
        while (oldData.moveToNext()) {
            // Get word from the old row.
            String word = oldData.getString(0);

            // Get the word's wordbook ID.
            String[] selectionArgs = {word};
            Cursor wordbookCursor = resolver.query(WordbookContract.CONTENT_URI, projection,
                    selection, selectionArgs, null);
            wordbookCursor.moveToFirst();
            int idIndex = wordbookCursor.getColumnIndexOrThrow(WordbookContract._ID);
            int id = wordbookCursor.getInt(idIndex);

            // Insert this item into the new WordbookFavorites table.
            ContentValues values = new ContentValues(2);
            values.put(AppDataContract.WordbookFavorites.COLUMN_NAME_WORDBOOK_ID, id);
            values.put(AppDataContract.WordbookFavorites.COLUMN_NAME_WORD, word);
            db.insert(AppDataContract.WordbookFavorites.TABLE_NAME, null, values);
        }
    }
}
