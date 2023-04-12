package com.dictionary.codebhak.wordbook;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.dictionary.codebhak.R;
import com.dictionary.codebhak.data.appdata.AppDataContract;
import com.dictionary.codebhak.data.appdata.WordbookHistoryProvider;

/**
 * A {@link AbstractWordbookListFragment} used to display a list of all words stored in
 * the wordbook history list.
 */
public class WordbookHistoryListFragment extends AbstractWordbookListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String NAME = "wordbook_History";

    private static final String[] PROJECTION = new String[] {AppDataContract.WordbookHistory._ID,
            AppDataContract.WordbookHistory.COLUMN_NAME_WORD};
    private static final String SELECTION = "";
    private static final String[] SELECTION_ARGS = {};
    private static final String ORDER_BY = AppDataContract.WordbookHistory._ID + " DESC";

    private SimpleCursorAdapter mAdapter;

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static final Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String fragmentName) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WordbookHistoryListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create and set list adapter.
        String[] fromColumns = {AppDataContract.WordbookHistory.COLUMN_NAME_WORD};
        int[] toViews = {android.R.id.text1};
        mAdapter = new android.widget.SimpleCursorAdapter(getActivity(),
                R.layout.lang_simple_list_item_activated_1, null, fromColumns, toViews, 0);
        setListAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), WordbookHistoryProvider.CONTENT_URI, PROJECTION, SELECTION,
                SELECTION_ARGS, ORDER_BY);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        setNoItemsView(R.string.wordbook_history_empty_view);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        Cursor cursor = (Cursor) mAdapter.getItem(position);
        int wordbookHistoryId = cursor.getInt(0);

        setSelectedWordbookItemId(wordbookHistoryId);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(NAME); // Database IDs start at 1.
    }

    @Override
    protected void setSelectedWordbookItemId(int id) {
        String[] columns = new String[] {AppDataContract.WordbookHistory.COLUMN_NAME_WORDBOOK_ID};
        String selection = AppDataContract.WordbookHistory._ID + " = ?";
        String[] selectionArgs = new String[] {Integer.toString(id)};
        Cursor cursor = getActivity().getContentResolver().query(AppDataContract.WordbookHistory.CONTENT_URI,
                columns, selection, selectionArgs, null);

        if (cursor.moveToFirst()) {
            mSelectedWordbookId = cursor.getInt(0);
        } else {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }
    }
}
