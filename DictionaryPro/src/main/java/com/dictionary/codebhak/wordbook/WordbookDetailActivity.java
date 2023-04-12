package com.dictionary.codebhak.wordbook;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.dictionary.codebhak.AbstractDetailActivity;
import com.dictionary.codebhak.R;
import com.dictionary.codebhak.MainActivity;
import com.dictionary.codebhak.SoundPlayer;
import com.dictionary.codebhak.data.appdata.AppDataContract;

/**
 * A {@link com.dictionary.codebhak.AbstractDetailActivity} used to display wordbook entries.
 */
public class WordbookDetailActivity extends AbstractDetailActivity {
    
    public static final String ARG_WORDBOOK_ID = "wordbook_id";
    public static final String ARG_WORD = "word";
    public static final String ARG_SOUND = "sound";
    
    private int mWordbookId;
    private String mWord;
    private String mSound;
    SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mWordbookId = intent.getIntExtra(ARG_WORDBOOK_ID, -1);
        mWord = intent.getStringExtra(ARG_WORD);
        mSound = intent.getStringExtra(ARG_SOUND);

        mTitle = getString(R.string.title_wordbook);
        soundPlayer = new SoundPlayer(getApplicationContext());

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(WordbookDetailFragment.ARG_ENTRY,
                    getIntent().getStringExtra(WordbookDetailFragment.ARG_ENTRY));
            WordbookDetailFragment fragment = new WordbookDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wordbook_detail_activity_menu, menu);
        setWordbookFavoriteIcon(menu);
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setWordbookFavoriteIcon(menu);
        restoreActionBar();
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Sets the Wordbook Favorite icon to the appropriate state based on the currently selected 
     * wordbook entry.
     * @param menu the {@code Menu} containing the Favorite icon
     */
    private void setWordbookFavoriteIcon(Menu menu) {
        MenuItem addFavorite = menu.findItem(R.id.action_add_favorite);
        MenuItem removeFavorite = menu.findItem(R.id.action_remove_favorite);

        // Hide both icons when no word is selected or the app is in one-pane mode.
        if (isFavorite(mWordbookId)) {
            addFavorite.setVisible(false);
            removeFavorite.setVisible(true);
        } else {
            addFavorite.setVisible(true);
            removeFavorite.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;
            case R.id.action_add_favorite:
                WordbookDetailFragment addFavoriteFragment 
                        = (WordbookDetailFragment) getFragmentManager()
                                .findFragmentById(R.id.item_detail_container);
                addFavoriteFragment.addWordbookFavorite(mWordbookId, mWord);
                return true;
            case R.id.action_sound:
                //this.mSound = "dummy.mp3";
                if (!this.mSound.equals("")){
                    soundPlayer.playSound(this.mSound);
                }

                return true;
            case R.id.action_remove_favorite:
                WordbookDetailFragment removeFavoriteFragment 
                        = (WordbookDetailFragment) getFragmentManager()
                                .findFragmentById(R.id.item_detail_container);
                removeFavoriteFragment.removeWordbookFavorite(mWordbookId);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Returns {@code true} if the word with the specified wordbook ID is 
     * a member of the favorites list.
     * @param wordbookId the wordbook ID to check
     * @return {@code true} if the specified word is a member of the
     *     favorites list, or {@code false} otherwise
     */
    private boolean isFavorite(int wordbookId) {
        String[] columns = new String[] {AppDataContract.WordbookFavorites._ID};
        String selection = AppDataContract.WordbookFavorites.COLUMN_NAME_WORDBOOK_ID + " = ?";
        String[] selectionArgs = new String[] {Integer.toString(wordbookId)};
        Cursor cursor = getContentResolver().query(AppDataContract.WordbookFavorites.CONTENT_URI,
                columns, selection, selectionArgs, null);
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
    // on some LG devices. Thanks to Alex Lockwood for the fix: http://stackoverflow.com/questions/26833242/nullpointerexception-phonewindowonkeyuppanel1002-main

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
