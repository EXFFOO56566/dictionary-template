package com.dictionary.codebhak.subdict;

import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.dictionary.codebhak.AbstractDetailActivity;
import com.dictionary.codebhak.MainActivity;
import com.dictionary.codebhak.R;
import com.dictionary.codebhak.data.appdata.AppDataContract;

/**
 * A {@link com.dictionary.codebhak.AbstractDetailActivity} used to display subdict sections.
 */
public class SubdictDetailActivity extends AbstractDetailActivity {

    public static final String ARG_SYNTAX_ID = "subdict_id";
    public static final String ARG_SECTION = "section";

    private static final String TAG = "SubdictDetailActivity";

    private CharSequence mTitle; // Used to store the last screen title.
    private int mSubdictId;
    private String mSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mSubdictId = intent.getIntExtra(ARG_SYNTAX_ID, -1);
        mSection = intent.getStringExtra(ARG_SECTION);

        mTitle = getString(R.string.title_subdict);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            String stringExtra = getIntent().getStringExtra(SubdictDetailFragment.ARG_XML);
            arguments.putString(SubdictDetailFragment.ARG_XML, stringExtra);
            SubdictDetailFragment fragment = new SubdictDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subdict_detail_menu, menu);
        setSubdictBookmarkIcon(menu);
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setSubdictBookmarkIcon(menu);
        restoreActionBar();
        return super.onPrepareOptionsMenu(menu);
    }

    private void setSubdictBookmarkIcon(Menu menu) {
        MenuItem addBookmark = menu.findItem(R.id.action_add_bookmark);
        MenuItem removeBookmark = menu.findItem(R.id.action_remove_bookmark);

        // Hide both icons when no word is selected or the app is in one-pane mode.
        if (isBookmark(mSubdictId)) {
            addBookmark.setVisible(false);
            removeBookmark.setVisible(true);
        } else {
            addBookmark.setVisible(true);
            removeBookmark.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager mgr = getFragmentManager();
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        case R.id.action_add_bookmark:
            SubdictDetailFragment addBookmarkFragment = 
                    (SubdictDetailFragment) mgr.findFragmentById(R.id.item_detail_container);
            addBookmarkFragment.addSubdictBookmark(mSubdictId, mSection);
            return true;
        case R.id.action_remove_bookmark:
            SubdictDetailFragment removeBookmarkFragment = 
                    (SubdictDetailFragment) mgr.findFragmentById(R.id.item_detail_container);
            removeBookmarkFragment.removeSubdictBookmark(mSubdictId);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Returns true if the word with the specified subdict ID is a member of the bookmarks list.
     * @param subdictId the subdict ID to check
     * @return true if the specified word is a member of the bookmarks list, or false otherwise
     */
    private boolean isBookmark(int subdictId) {
        Log.w(TAG, "isBookmark(); id: " + subdictId);
        String[] columns = new String[] {AppDataContract.SubdictBookmarks._ID};
        String selection = AppDataContract.SubdictBookmarks.COLUMN_NAME_SYNTAX_ID + " = ?";
        String[] selectionArgs = new String[] {Integer.toString(subdictId)};
        Uri uri = AppDataContract.SubdictBookmarks.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, columns, selection, selectionArgs, null);
        boolean result = false;
        if (cursor.getCount() > 0) {
            result = true;
        }
        cursor.close();
        return result;
    }

    @Override
    protected void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    // The following two methods are a workaround for a bug related to the appcompat-v7 library
    // on some LG devices. Thanks to Alex Lockwood for the fix: 
    // http://stackoverflow.com/questions/26833242/nullpointerexception-phonewindowonkeyuppanel1002-main

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_MENU == keyCode && Build.BRAND.equalsIgnoreCase("LGE")) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_MENU == keyCode && Build.BRAND.equalsIgnoreCase("LGE")) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}

