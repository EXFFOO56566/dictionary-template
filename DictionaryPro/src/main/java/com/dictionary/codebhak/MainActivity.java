package com.dictionary.codebhak;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dictionary.codebhak.R;
import com.dictionary.codebhak.data.appdata.AppDataContract;
import com.dictionary.codebhak.data.appdata.WordbookHistoryProvider;
import com.dictionary.codebhak.data.wordbook.WordbookContract;
import com.dictionary.codebhak.data.wordbook.WordbookProvider;
import com.dictionary.codebhak.data.subdict.SubdictContract;
import com.dictionary.codebhak.wordbook.AbstractWordbookListFragment;
import com.dictionary.codebhak.wordbook.WordbookBrowseListFragment;
import com.dictionary.codebhak.wordbook.WordbookDetailActivity;
import com.dictionary.codebhak.wordbook.WordbookDetailFragment;
import com.dictionary.codebhak.wordbook.WordbookFavoritesListFragment;
import com.dictionary.codebhak.wordbook.WordbookHistoryListFragment;
import com.dictionary.codebhak.navigationdrawer.NavigationDrawerFragment;
import com.dictionary.codebhak.subdict.AbstractSubdictListFragment;
import com.dictionary.codebhak.subdict.SubdictBookmarksListFragment;
import com.dictionary.codebhak.subdict.SubdictBrowseListFragment;
import com.dictionary.codebhak.subdict.SubdictDetailActivity;
import com.dictionary.codebhak.subdict.SubdictDetailFragment;


/**
 * The app's primary activity. On tablets, this activity displays a two-pane layout containing an 
 * {@link AbstractListFragment} and an {@link AbstractDetailFragment}. On phones, it displays only 
 * an {@code AbstractListFragment}.
 */
public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, 
                AbstractListFragment.Callbacks {
    
    /** Intent bundle key. */
    public static final String KEY_MODE = "mode";
    
    /** Custom intent action. */
    public static final String ACTION_SET_MODE = "com.dictionary.codebhak.SET_MODE";
    
    private static final String TAG = "MainActivity";

    // Application state bundle keys
    private static final String KEY_TITLE = "action_bar_title";
    private static final String KEY_SUBTITLE = "action_bar_subtitle";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private CharSequence mSubtitle;
    private Mode mMode;
    
    /** Indicates whether we're using the tablet layout. */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Set the toolbar to act as the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        // Restore any saved state.
        if (null == savedInstanceState) {
            mTitle = getString(R.string.title_wordbook);
            mSubtitle = getString(R.string.title_wordbook_browse);
        } else {
            mTitle = savedInstanceState.getString(KEY_TITLE);
            mSubtitle = savedInstanceState.getString(KEY_SUBTITLE);
            mMode = Mode.getModeFromName(savedInstanceState.getString(KEY_MODE));
        }
        
        // Set the status bar background color.
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.green_accent_dark));

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.navigation_drawer_fragment);

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer_fragment_container,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
        
        restoreActionBar();

        checkTabletDisplayMode();
        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkTabletDisplayMode();
    }

    /**
     * If the app is running on a large screen, sets the display mode to either one-pane or
     * two-pane depending on the setting stored in the app preferences and the currently selected
     * navigation drawer mode. This method does no work on phones, since one-pane mode is always
     * used on small screens.
     */
    private void checkTabletDisplayMode() {
        if (!mTwoPane) {
            return;
        }
        View leftPane = findViewById(R.id.item_list_container);
        if (onePaneModeSelected() && mMode.equals(Mode.WORDBOOK_BROWSE)) {
            leftPane.setVisibility(View.GONE);
        } else {
            leftPane.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Checks whether the user has selected the one-pane mode preference. This method always
     * returns false on phones.
     * @return true if the user has selected the one-pane mode preference or false otherwise
     */
    private boolean onePaneModeSelected() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String key = getString(R.string.pref_onePane_key);
        return prefs.getBoolean(key, false);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TITLE, (String) mTitle);
        outState.putString(KEY_SUBTITLE, (String) mSubtitle);
        outState.putString(KEY_MODE, mMode.getName());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTitle = savedInstanceState.getString(KEY_TITLE);
        mSubtitle = savedInstanceState.getString(KEY_SUBTITLE);
        mMode = Mode.getModeFromName(savedInstanceState.getString(KEY_MODE));
        restoreActionBar();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * Processes an <code>Intent</code> if it can be handled by this {@code Activity} or
     * throws an exception if this {@code Activity} cannot handle the specified {@code Intent}.
     * @param intent the {@code Intent} to handle
     */
    void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra((SearchManager.QUERY));
            search(query);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri data = intent.getData();
            getWordbookEntry(data);
        } else if (ACTION_SET_MODE.equals(intent.getAction())) {
            String modeName = intent.getStringExtra(KEY_MODE);
            Mode mode = Mode.getModeFromName(modeName);
            switchToMode(mode);
        }
    }

    /**
     * Callback method from {@link AbstractListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String fragmentName) {
        switch (fragmentName) {
        case WordbookBrowseListFragment.NAME:
        case WordbookFavoritesListFragment.NAME:
        case WordbookHistoryListFragment.NAME:
            wordbookItemSelected();
            break;
        case SubdictBrowseListFragment.NAME:
        case SubdictBookmarksListFragment.NAME:
            subdictItemSelected();
            break;
        default:
            throw new IllegalArgumentException("Invalid fragment name");
        }
        invalidateOptionsMenu();
    }

    /**
     * Retrieves and displays the currently selected wordbook item's entry.
     */
    private void wordbookItemSelected() {
        // TODO: Verify that we're in the correct mode here and in similar
        // situations through this class and throw an exception if we're not.

        
        FragmentManager mgr = getFragmentManager();
        AbstractWordbookListFragment fragment = 
                (AbstractWordbookListFragment) mgr.findFragmentById(R.id.item_list_container);
        String id = Integer.toString(fragment.getSelectedWordbookId());

        String[] columns = new String[] {
            WordbookContract.COLUMN_ENTRY,
            WordbookContract.COLUMN_LANG_FULL_WORD,
            WordbookContract.COLUMN_SOUND
        };
        String selection = WordbookContract._ID + " = ?";
        String[] selectionArgs = new String[] {id};
        Uri uri = WordbookContract.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, columns, selection, selectionArgs, null);

        String entry;
        String word;
        String sound;
        if (cursor.moveToFirst()) {
            entry = cursor.getString(0);
            word = cursor.getString(1);
            sound = cursor.getString(2);
        } else {
            throw new IllegalStateException("Failed to retrieve wordbook entry");
        }

        displayWordbookEntry(id, word, entry,sound);
    }

    /**
     * Retrieves and displays the currently selected Overview of Lang Subdict
     * item's entry.
     */
    private void subdictItemSelected() {
        FragmentManager mgr = getFragmentManager();;
        AbstractSubdictListFragment fragment = 
                (AbstractSubdictListFragment) mgr.findFragmentById(R.id.item_list_container);

        String[] columns = new String[] {
            SubdictContract.COLUMN_NAME_XML,
            SubdictContract.COLUMN_NAME_SECTION
        };
        String selection = SubdictContract._ID + " = ?";
        String id = Integer.toString(fragment.getSelectedSubdictId());
        String[] selectionArgs = new String[] {id};
        Uri uri = SubdictContract.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, columns, selection, selectionArgs, null);

        String xml;
        String section;
        if (cursor.moveToFirst()) {
            xml = cursor.getString(0);
            section = cursor.getString(1);
            Log.w("Subdict item selected", section + ": " + xml);
        } else {
            throw new IllegalStateException("Failed to retrieve subdict section");
        }

        displaySubdictSection(section, xml);
    }

    /**
     * Displays the specified wordbook entry in a {@link WordbookDetailFragment}.
     * @param id the wordbook database ID of the selected entry
     * @param word the word whose entry is selected
     * @param entry the selected entry's XML
     */
    void displayWordbookEntry(final String id, String word, String entry, String sound) {
        // If user searches from Quick Search Box, we may need to change mode.
        if (!mMode.equals(Mode.WORDBOOK_BROWSE) 
                && !mMode.equals(Mode.WORDBOOK_FAVORITES)
                && !mMode.equals(Mode.WORDBOOK_HISTORY)) {
            switchToWordbookBrowse();
        }

        // Add entry to history, unless word was selected from history list.
        if (!mMode.equals(Mode.WORDBOOK_HISTORY)) {
            addHistory(id, word);
        }

        // Display entry.
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(WordbookDetailFragment.ARG_ENTRY, entry);
            WordbookDetailFragment fragment = new WordbookDetailFragment();
            fragment.setArguments(arguments);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.item_detail_container, fragment);
            transaction.commitAllowingStateLoss();
        } else {
            FragmentManager mgr = getFragmentManager();
            AbstractWordbookListFragment fragment = 
                    (AbstractWordbookListFragment) mgr.findFragmentById(R.id.item_list_container);
            Intent intent = new Intent(this, WordbookDetailActivity.class);
            intent.putExtra(WordbookDetailFragment.ARG_ENTRY, entry);
            int wordbookId = fragment.getSelectedWordbookId();
            intent.putExtra(WordbookDetailActivity.ARG_WORDBOOK_ID, wordbookId);
            intent.putExtra(WordbookDetailActivity.ARG_WORD, word);
            intent.putExtra(WordbookDetailActivity.ARG_SOUND, sound);
            startActivity(intent);
        }
    }

    /**
     * Displays the specified Overview of Lang Subdict section in a {@link SubdictDetailFragment}.
     * @param section the selected section's title
     * @param xml the selected section's XML
     */
    void displaySubdictSection(String section, String xml) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(SubdictDetailFragment.ARG_XML, xml);
            SubdictDetailFragment fragment = new SubdictDetailFragment();
            fragment.setArguments(arguments);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.item_detail_container, fragment);
            transaction.commitAllowingStateLoss();
        } else {
            FragmentManager mgr = getFragmentManager();
            AbstractSubdictListFragment fragment = 
                    (AbstractSubdictListFragment) mgr.findFragmentById(R.id.item_list_container);
            Intent intent = new Intent(this, SubdictDetailActivity.class);
            intent.putExtra(SubdictDetailFragment.ARG_XML, xml);
            int subdictId = fragment.getSelectedSubdictId();
            intent.putExtra(SubdictDetailActivity.ARG_SYNTAX_ID, subdictId);
            intent.putExtra(SubdictDetailActivity.ARG_SECTION, section);
            startActivity(intent);
        }
    }

    /**
     * Adds the specified word to the wordbook history list. If the word is already contained in the
     * list, it will be moved to the top of the list.
     * @param id the wordbook database ID of the selected word
     * @param word the selected word
     */
    void addHistory(String id, String word) {
        // If the word is already in the list, delete it.
        String selection = AppDataContract.WordbookHistory.COLUMN_NAME_WORDBOOK_ID + " = ?";
        String[] selectionArgs = {id};
        getContentResolver().delete(WordbookHistoryProvider.CONTENT_URI, selection, selectionArgs);

        // Add word to top of list.
        ContentValues values = new ContentValues();
        values.put(AppDataContract.WordbookHistory.COLUMN_NAME_WORDBOOK_ID, id);
        values.put(AppDataContract.WordbookHistory.COLUMN_NAME_WORD, word);
        getContentResolver().insert(WordbookHistoryProvider.CONTENT_URI, values);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
         // We consider the user to have learned the drawer once he or she selects an item. This
         // prevents the drawer from appearing repeatedly in the one-pane mode. This is just a quick
         // workaround; we might want to implement a more sophisticated solution at some point in
         // the future.
        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.userLearnedDrawer();
        }

        switchToMode(Mode.getModeFromPosition(position));
    }

    @Override
    public void onNavigationDrawerChangeCat(String cat) {
        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.userLearnedDrawer();
        }
        switchToMode(Mode.getModeFromPosition(1));
        //change cate here
    }


    /**
     * Sets the navigation bar navigation mode and title to the appropriate values.
     */
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        
        // The action bar will be null when this is called from NavigationDrawerFragment's 
        // constructor. We call this method again near the end of this class's constructor to 
        // set the action bar title.
        // TODO: Find a more elegant way to handle this.
        if (null == actionBar) {
            return;
        }
        
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setSubtitle(mSubtitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen if the drawer is not showing.
        // Otherwise, let the drawer decide what to show in the action bar.
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            return super.onCreateOptionsMenu(menu);
        }

        // Inflate the options menu from XML. We have to handle the menu here rather than in the
        // fragment so that we can hide them when the navigation drawer is open.
        if (mMode.isWordbookMode()) {
            getMenuInflater().inflate(R.menu.wordbook_menu, menu);
            setWordbookFavoriteIcon(menu);

            // Get the SearchView and set the searchable configuration.
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            MenuItem searchItem = menu.findItem(R.id.menu_search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

            restoreActionBar();
            return super.onCreateOptionsMenu(menu);
        }else if (mMode.isCateMode()) {
            //Log.d("ThangTB", "---------- is category mode");
            return super.onCreateOptionsMenu(menu);
        } else if (mMode.isSubdictMode()) {
            getMenuInflater().inflate(R.menu.subdict_menu, menu);
            setSubdictBookmarkIcon(menu);
            restoreActionBar();
            return super.onCreateOptionsMenu(menu);
        } else {
            throw new IllegalStateException("Invalid mode");
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen if the drawer is not showing.
        // Otherwise, let the drawer decide what to show in the action bar.
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            return super.onCreateOptionsMenu(menu);
        }

        if (mMode.isWordbookMode()) {
            setWordbookFavoriteIcon(menu);
        } else if (mMode.isSubdictMode()) {
            setSubdictBookmarkIcon(menu);
        }else if (mMode.isCateMode()) {
            //Log.d("ThangTB", "---------- is category mode");
        } else {
            throw new IllegalStateException("Invalid mode");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: Move favorite and bookmark code to fragments?
        // TODO: Move favorite and bookmark options to fragments?
        FragmentManager mgr = getFragmentManager();
        switch (item.getItemId()) {
        case R.id.action_sound:
            WordbookDetailFragment playsound =
                    (WordbookDetailFragment) mgr.findFragmentById(R.id.item_detail_container);
            playsound.playSound();
            return true;
        case R.id.action_add_favorite:
            WordbookDetailFragment addFavoriteFragment =
                    (WordbookDetailFragment) mgr.findFragmentById(R.id.item_detail_container);
            addFavoriteFragment.addWordbookFavorite();
            return true;
        case R.id.action_remove_favorite:
            WordbookDetailFragment removeFavoriteFragment =
                    (WordbookDetailFragment) mgr.findFragmentById(R.id.item_detail_container);
            removeFavoriteFragment.removeWordbookFavorite();
            return true;
        case R.id.action_add_bookmark:
            SubdictDetailFragment addBookmarkFragment
                    = (SubdictDetailFragment) mgr.findFragmentById(R.id.item_detail_container);
            addBookmarkFragment.addSubdictBookmark();
            return true;
        case R.id.action_remove_bookmark:
            SubdictDetailFragment removeBookmarkFragment
                    = (SubdictDetailFragment) mgr.findFragmentById(R.id.item_detail_container);
            removeBookmarkFragment.removeSubdictBookmark();
            return true;
        case R.id.action_clear_history:
            clearHistory();
            return true;
        case R.id.action_clear_favorites:
            clearWordbookFavorites();
            return true;
        case R.id.action_clear_bookmarks:
            clearSubdictBookmarks();
            return true;
        case R.id.action_settings:
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        case R.id.action_help:
            displayHelp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Deletes all words from the wordbook history list.
     */
    private void clearHistory() {
        getContentResolver().delete(AppDataContract.WordbookHistory.CONTENT_URI, null, null);
        String msg = getString(R.string.toast_clear_history);
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Deletes all words from the wordbook favorites list.
     */
    private void clearWordbookFavorites() {
        DialogFragment dialog = new ClearWordbookFavoritesDialogFragment();
        dialog.show(getFragmentManager(), "clearFavorites");
    }

    /**
     * A {@link DialogFragment} that asks the user to confirm that he or she wishes to clear the
     * wordbook favorites list. If the user answers in the affirmative, the list is cleared.
     * Otherwise, the dialog is dismissed and no further action is taken.
     */
    public static class ClearWordbookFavoritesDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.clear_wordbook_favorites_dialog_message);
            
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ContentResolver resolver = getActivity().getContentResolver();
                    resolver.delete(AppDataContract.WordbookFavorites.CONTENT_URI, null, null);

                    String msg = getString(R.string.toast_clear_wordbook_favorites);
                    Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            
            return builder.create();
        }
    }

    /**
     * Deletes all items from the subdict bookmarks list.
     */
    private void clearSubdictBookmarks() {
        DialogFragment dialog = new ClearSubdictBookmarksDialogFragment();
        dialog.show(getFragmentManager(), "clearBookmarks");
    }

    /**
     * A {@link DialogFragment} that asks the user to confirm that he or she wishes to clear the
     * subdict bookmarks list. If the user answers in the affirmative, the list is cleared.
     * Otherwise, the dialog is dismissed and no further action is taken.
     */
    public static class ClearSubdictBookmarksDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.clear_subdict_bookmarks_dialog_message);
            
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity activity = (MainActivity) getActivity();
                    
                    Uri uri = AppDataContract.SubdictBookmarks.CONTENT_URI;
                    activity.getContentResolver().delete(uri, null, null);

                    String msg = getString(R.string.toast_clear_subdict_bookmarks);
                    Context context = activity.getApplicationContext();
                    Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            
            return builder.create();
        }
    }

    /**
     * Finds and selects the wordbook entry corresponding to the specified URI.
     * @param data the URI of the wordbook entry to select
     */
    private void getWordbookEntry(Uri data) {
        ensureModeIsWordbookBrowse();

        // Get data.
        Cursor cursor = getContentResolver().query(data, null, null, null, null);
        cursor.moveToFirst();
        String id;
        try {
            int idIndex = cursor.getColumnIndexOrThrow(WordbookContract._ID);
            id = cursor.getString(idIndex);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Failed to retrieve result from database.");
            throw e;
        }

        // Set this item's state to activated on tablets and scroll the list to the item.
        FragmentManager mgr = getFragmentManager();
        WordbookBrowseListFragment fragment = 
                (WordbookBrowseListFragment) mgr.findFragmentById(R.id.item_list_container);
        fragment.selectItem(Integer.parseInt(id));
    }

    /**
     * Searches the wordbook for a word and displays the result.
     * @param query a string containing the word for which to search. This string is case
     *     insensitive and may be written in either Lang characters or Beta code.
     */
    // TODO: Handle words with multiple entries.
    void search(String query) {
        String[] columns = new String[] {WordbookContract._ID};
        String selection = WordbookContract.COLUMN_LANG_FULL_WORD + " = ?";
        String[] selectionArgs = new String[] {query};
        Log.d("ThangTB","searching..."+query);
        String sortOrder = WordbookContract._ID + " ASC";
        ContentResolver resolver = getContentResolver();
        Uri uri = WordbookProvider.CONTENT_URI;
        Cursor cursor = resolver.query(uri, columns, selection, selectionArgs, sortOrder);

        if (cursor.moveToFirst()) {
            String id = cursor.getString(0);
            ensureModeIsWordbookBrowse();
            FragmentManager mgr = getFragmentManager();
            WordbookBrowseListFragment fragment = 
                    (WordbookBrowseListFragment) mgr.findFragmentById(R.id.item_list_container);
            fragment.selectItem(Integer.parseInt(id));
        } else {
            String msg = getString(R.string.toast_search_no_results);
            Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
            toast.show();
        }

        cursor.close();
    }

    /**
     * Switches the mode to Wordbook Browse if that is not the current mode.
     */
    private void ensureModeIsWordbookBrowse() {
        if (!mMode.equals(Mode.WORDBOOK_BROWSE)) {
            switchToWordbookBrowse();

            // Make sure the fragments are swapped before we try to get the
            // WordbookBrowseListFragment.
            getFragmentManager().executePendingTransactions();
        }
    }

    /**
     * Switches the mode to the specified {@link Mode}.
     * @param mode the {@code Mode} to which to switch
     */
    private void switchToMode(Mode mode) {
        switch (mode) {
        case WORDBOOK_BROWSE:
            switchToWordbookBrowse();
            break;
        case WORDBOOK_FAVORITES:
            switchToWordbookFavorites();
            break;
        case WORDBOOK_HISTORY:
            switchToWordbookHistory();
            break;
        case SYNTAX_BROWSE:
            switchToSubdictBrowse();
            break;
        case SYNTAX_BOOKMARKS:
            switchToSubdictBookmarks();
            break;
        default:
            throw new IllegalArgumentException("Invalid mode");
        }
        
        // Make sure we're showing or hiding the left pane appropriately.
        checkTabletDisplayMode(); 
    }

    private void switchToWordbookBrowse() {
        mMode = Mode.WORDBOOK_BROWSE;
        mTitle = getString(R.string.title_wordbook);
        mSubtitle = getString(R.string.title_wordbook_browse);
        restoreActionBar();
        swapInFragments(new WordbookBrowseListFragment(), new WordbookDetailFragment());
        ensureNavDrawerSelection(Mode.WORDBOOK_BROWSE);
    }

    private void switchToWordbookFavorites() {
        mMode = Mode.WORDBOOK_FAVORITES;
        mTitle = getString(R.string.title_wordbook);
        mSubtitle = getString(R.string.title_wordbook_favorites);
        restoreActionBar();
        swapInFragments(new WordbookFavoritesListFragment(), new WordbookDetailFragment());
        ensureNavDrawerSelection(Mode.WORDBOOK_FAVORITES);
    }

    private void switchToWordbookHistory() {
        mMode = Mode.WORDBOOK_HISTORY;
        mTitle = getString(R.string.title_wordbook);
        mSubtitle = getString(R.string.title_wordbook_history);
        restoreActionBar();
        swapInFragments(new WordbookHistoryListFragment(), new WordbookDetailFragment());
        ensureNavDrawerSelection(Mode.WORDBOOK_HISTORY);
    }

    private void switchToSubdictBrowse() {
        mMode = Mode.SYNTAX_BROWSE;
        mTitle = getString(R.string.title_subdict);
        mSubtitle = getString(R.string.title_subdict_browse);
        restoreActionBar();
        swapInFragments(new SubdictBrowseListFragment(), new SubdictDetailFragment());
        ensureNavDrawerSelection(Mode.SYNTAX_BROWSE);
    }

    private void switchToSubdictBookmarks() {
        mMode = Mode.SYNTAX_BOOKMARKS;
        mTitle = getString(R.string.title_subdict);
        mSubtitle = getString(R.string.title_subdict_bookmarks);
        restoreActionBar();
        swapInFragments(new SubdictBookmarksListFragment(), new SubdictDetailFragment());
        ensureNavDrawerSelection(Mode.SYNTAX_BOOKMARKS);
    }

    /**
     * Sets the selected navigation drawer position to the position corresponding to the
     * current mode.
     * @param mode the {@link Mode} to which to set the navigation drawer selection
     */
    private void ensureNavDrawerSelection(Mode mode) {
        // If the nav drawer hasn't been created yet, we don't need to worry about this.
        if (null == mNavigationDrawerFragment) {
            return;
        }

        int position = mNavigationDrawerFragment.getCurrentSelectedPosition();
        Mode navDrawerMode = Mode.getModeFromPosition(position);
        if (!navDrawerMode.equals(mode)) {
            mNavigationDrawerFragment.setCurrentSelectedPosition(mode.getPosition());
        }
    }

    /**
     * Replaces the currently displayed fragment(s) with the specified fragment(s).
     * @param listFragment the {@link AbstractListFragment} to swap in
     * @param detailFragment the {@link AbstractDetailFragment} to swap in, or null if the app is in
     *     one-pane mode
     */
    private void swapInFragments(Fragment listFragment, Fragment detailFragment) {
        // TODO: Check for invalid null arguments.
        if (mTwoPane) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.item_list_container, listFragment);
            transaction.replace(R.id.item_detail_container, detailFragment);
            transaction.commit();
        } else {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.item_list_container, listFragment);
            transaction.commit();
        }
    }

    public boolean isTwoPane() {
        return mTwoPane;
    }

    /**
     * Sets the Wordbook Favorite icon to the appropriate state based on the currently selected
     * wordbook entry.
     * @param menu the {@code Menu} containing the Favorite icon
     */
    private void setWordbookFavoriteIcon(Menu menu) {
        FragmentManager mgr = getFragmentManager();
        AbstractWordbookListFragment fragment
                = (AbstractWordbookListFragment) mgr.findFragmentById(R.id.item_list_container);

        MenuItem addFavorite = menu.findItem(R.id.action_add_favorite);
        MenuItem sound = menu.findItem(R.id.action_sound);
        MenuItem removeFavorite = menu.findItem(R.id.action_remove_favorite);

        if (mTwoPane){
            sound.setVisible(true);
        }else{
            sound.setVisible(false);
        }
        if (fragment.nothingIsSelected() || !mTwoPane) {
            addFavorite.setVisible(false);
            removeFavorite.setVisible(false);
        } else if (fragment.selectedWordIsFavorite()) {
            addFavorite.setVisible(false);
            removeFavorite.setVisible(true);
        } else {
            addFavorite.setVisible(true);
            removeFavorite.setVisible(false);
        }
    }

    /**
     * Sets the Subdict Bookmark icon to the appropriate state based on the currently selected
     * subdict section.
     * @param menu the {@code Menu} containing the Bookmark icon
     */
    private void setSubdictBookmarkIcon(Menu menu) {
        FragmentManager mgr = getFragmentManager();
        AbstractSubdictListFragment fragment = 
                (AbstractSubdictListFragment) mgr.findFragmentById(R.id.item_list_container);

        MenuItem addBookmark = menu.findItem(R.id.action_add_bookmark);
        MenuItem removeBookmark = menu.findItem(R.id.action_remove_bookmark);

        if (fragment.nothingIsSelected() || !mTwoPane) {
            addBookmark.setVisible(false);
            removeBookmark.setVisible(false);
        } else if (fragment.selectedSectionIsBookmarked()) {
            addBookmark.setVisible(false);
            removeBookmark.setVisible(true);
        } else {
            addBookmark.setVisible(true);
            removeBookmark.setVisible(false);
        }
    }

    /**
     * Launches an email app that the user can use to send feedback about this app.
     */
    private void sendFeedback() {
        Uri uri = Uri.fromParts("mailto", getString(R.string.feedback_email), null);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
        startActivity(Intent.createChooser(intent, getString(R.string.feedback_intent_chooser)));
    }

    /**
     * A {@link DialogFragment} containing help text.
     */
    public static class HelpDialogFragment extends DialogFragment {
        // TODO: Either make these dialog fragment classes private or reuse a single one everywhere.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.title_help);

            TextView textView = new TextView(getActivity());
            textView.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            textView.setPadding(25, 25, 25, 25);
            textView.setText(Html.fromHtml(getString(R.string.message_help)));
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            
            ScrollView scrollView = new ScrollView(getActivity());
            scrollView.addView(textView);
            builder.setView(scrollView);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            return builder.create();
        }
    }

    /**
     * Displays a dialog fragment containing help text.
     */
    private void displayHelp() {
        HelpDialogFragment dialogFragment = new HelpDialogFragment();
        dialogFragment.show(getFragmentManager(), "help");
    }

    public Mode getMode() {
        return mMode;
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
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (KeyEvent.KEYCODE_MENU == keyCode && Build.BRAND.equalsIgnoreCase("LGE")) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
