package com.dictionary.codebhak.wordbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dictionary.codebhak.AbstractDetailFragment;
import com.dictionary.codebhak.R;
import com.dictionary.codebhak.LangTextView;
import com.dictionary.codebhak.SoundPlayer;
import com.dictionary.codebhak.data.appdata.AppDataContract;
import com.dictionary.codebhak.data.wordbook.WordbookContract;
import com.dictionary.codebhak.data.wordbook.WordbookEntry;
import com.dictionary.codebhak.data.wordbook.WordbookXmlParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.net.Uri;
import android.text.Html;
import android.text.Spanned;

/**
 * A {@link com.dictionary.codebhak.AbstractDetailFragment} used to display a wordbook entry.
 */
public class WordbookDetailFragment extends AbstractDetailFragment {

    public static final String TAG = "WordbookDetailFragment";
    public static final String VIEWONWEB_TOOL_EXTRA_KEY = "com.dictionary.codebhak.wordbook.ViewonwebToolExtraKey";

    // Fragment arguments representing strings containing entry information
    public static final String ARG_ENTRY = "entry";

    //    private WordbookEntry mWordbookEntry = null;
    private boolean mBlank = true; // True if no entry displayed.
    private SoundPlayer soundPlayer;
    String strHtml="";
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WordbookDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments() != null && getArguments().containsKey(ARG_ENTRY)) {
            mBlank = false;

            // Load entry represented by fragment argument.
            String entry = getArguments().getString(ARG_ENTRY);
            strHtml = entry;
            // Parse XML.
        }
        soundPlayer = new SoundPlayer(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        if (!mBlank) {
            // Display wordbook entry.
            LangTextView textView = (LangTextView) rootView.findViewById(R.id.item_detail);
            textView.setText(Html.fromHtml(strHtml));
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.wordbook_detail_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
//        MenuItem item = menu.findItem(R.id.action_viewonweb_tool);
//        if (viewonwebToolOptionDisabled()) {
//            item.setVisible(false);
//        } else {
//            item.setVisible(true);
//        }
    }

    /**
     * Checks whether the user has disabled the View on Viewonweb option in the settings.
     * @return {@code true} if the user has disabled the option or {@code false}
     *     otherwise
     */
    private boolean viewonwebToolOptionDisabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return prefs.getBoolean(getString(R.string.pref_viewonweb_tool_key), false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_viewonweb_tool:
                displayViewonwebTool();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Adds the specified word to the wordbook favorites list.
     * @param wordbookId the wordbook ID of the word to add
     * @param word the word to add
     */
    protected void addWordbookFavorite(int wordbookId, String word) {
        ContentValues values = new ContentValues();
        values.put(AppDataContract.WordbookFavorites.COLUMN_NAME_WORDBOOK_ID, wordbookId);
        values.put(AppDataContract.WordbookFavorites.COLUMN_NAME_WORD, word);
        getActivity().getContentResolver().insert(AppDataContract.WordbookFavorites.CONTENT_URI, values);
        getActivity().invalidateOptionsMenu();
        displayToast(getString(R.string.toast_favorite_added));
    }

    /**
     * Removes the specified word from the wordbook favorites list.
     * @param wordbookId the wordbook ID of the word to remove
     */
    protected void removeWordbookFavorite(int wordbookId) {
        String selection = AppDataContract.WordbookFavorites.COLUMN_NAME_WORDBOOK_ID + " = ?";
        String[] selectionArgs = {Integer.toString(wordbookId)};
        getActivity().getContentResolver()
                .delete(AppDataContract.WordbookFavorites.CONTENT_URI, selection, selectionArgs);
        getActivity().invalidateOptionsMenu();
        displayToast(getString(R.string.toast_favorite_removed));
    }

    // NOTE: The following two methods should only be used in two-pane mode.
    // TODO: Throw exception if these methods are called in one-pane mode.


    public void playSound() {
        AbstractWordbookListFragment fragment = (AbstractWordbookListFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.item_list_container);
        int wordbookId = fragment.getSelectedWordbookId();
        String soundName = getSoundFromWordbookId(wordbookId);
        if (!soundName.equals("")){
            soundPlayer.playSound(soundName);
        }
    }

    /**
     * Adds the currently selected word to the wordbook favorites list.
     */
    public void addWordbookFavorite() {
        AbstractWordbookListFragment fragment = (AbstractWordbookListFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.item_list_container);
        int wordbookId = fragment.getSelectedWordbookId();
        String word = getWordFromWordbookId(wordbookId);
        addWordbookFavorite(wordbookId, word);
    }

    /**
     * Removes the currently selected word from the wordbook favorites list.
     */
    public void removeWordbookFavorite() {
        AbstractWordbookListFragment fragment = (AbstractWordbookListFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.item_list_container);
        int wordbookId = fragment.getSelectedWordbookId();
        removeWordbookFavorite(wordbookId);
    }

    /**
     * Returns the word corresponding to the specified wordbook ID.
     * @param id the wordbook ID for which to search
     * @return the corresponding word
     */
    private String getWordFromWordbookId(int id) {
        String[] projection = {WordbookContract.COLUMN_LANG_FULL_WORD};
        String selection = WordbookContract._ID + " = ?";
        String[] selectionArgs = {Integer.toString(id)};
        Cursor cursor = getActivity().getContentResolver()
                .query(WordbookContract.CONTENT_URI, projection, selection, selectionArgs, null);
        String word;
        if (cursor.moveToFirst()) {
            word = cursor.getString(0);
        } else {
            throw new IllegalArgumentException("Invalid wordbook ID: " + id);
        }
        return word;
    }
    /**
     * Returns the word corresponding to the specified wordbook ID.
     * @param id the wordbook ID for which to search
     * @return the corresponding word
     */
    private String getSoundFromWordbookId(int id) {
        String[] projection = {WordbookContract.COLUMN_SOUND};
        String selection = WordbookContract._ID + " = ?";
        String[] selectionArgs = {Integer.toString(id)};
        Cursor cursor = getActivity().getContentResolver()
                .query(WordbookContract.CONTENT_URI, projection, selection, selectionArgs, null);
        String word;
        if (cursor.moveToFirst()) {
            word = cursor.getString(0);
        } else {
            throw new IllegalArgumentException("Invalid wordbook ID: " + id);
        }
        return word;
    }
    /**
     * Searches for this word in the Viewonweb Greek Word Study Tool and displays the resulting
     * page in a <code>WebView</code>, or displays an error dialog if there is no network
     * connection.
     */
    private void displayViewonwebTool() {
        if (!networkConnectionAvailable()) {
            displayNoNetworkConnectionError();
            return;
        }
        WordbookEntry mWordbookEntry = null;
        // Parse XML.
           WordbookXmlParser parser = new WordbookXmlParser();
           InputStream in = new ByteArrayInputStream(strHtml.getBytes());

         try {
              mWordbookEntry = parser.parse(in);
          } catch (Exception e) {
              Log.e(TAG, "Error parsing entry: " + e);
             Log.e(TAG, Log.getStackTraceString(e));
    }
        Intent intent = new Intent(getActivity(), ViewonwebToolActivity.class);
        String morph = mWordbookEntry.getOrth();
        intent.putExtra(VIEWONWEB_TOOL_EXTRA_KEY, morph);
        startActivity(intent);
    }

    /**
     * Checks whether the device is connected to the Internet.
     * @return {@code true} if the device is connected to the Internet or {@code false}
     *     otherwise
     */
    private boolean networkConnectionAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Displays an error dialog that explains that a network connection is required to use the
     * selected feature.
     */
    private void displayNoNetworkConnectionError() {
        new NoNetworkConnectionDialogFragment().show(getFragmentManager(), null);
    }

    public static class NoNetworkConnectionDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_no_network_connection)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
            return builder.create();
        }
    }
}
