package com.dictionary.codebhak.subdict;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.dictionary.codebhak.data.subdict.SubdictContract;

/**
 * A {@link AbstractSubdictListFragment} used to display a list of all sections in the Overview of Lang
 * Subdict text.
 */
public class SubdictBrowseListFragment extends AbstractSubdictListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String NAME = "subdict_browse";

    private SimpleCursorAdapter mAdapter;
    private static final String[] PROJECTION = new String[] {
        SubdictContract._ID,
        SubdictContract.COLUMN_NAME_SECTION
    };
    private static final String SELECTION = "";
    private static final String[] SELECTION_ARGS = {};

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static final Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String fragmentName) {}
    };
    
    /** The fragment's current callback object, which is notified of list item clicks. */
    private Callbacks mCallbacks = sDummyCallbacks;

    /** The current activated item position. Only used on tablets. */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SubdictBrowseListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Create a custom adapter with support for chapter headings.
        String[] fromColumns = {SubdictContract.COLUMN_NAME_SECTION};
        int[] toViews = {android.R.id.text1};
        int layout = android.R.layout.simple_list_item_activated_1;
        mAdapter = new SimpleCursorAdapter(getActivity(), layout, null, fromColumns, toViews, 0);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), SubdictContract.CONTENT_URI, PROJECTION, SELECTION,
                SELECTION_ARGS, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
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
        setSelectedSubdictItemId(position);
        mCallbacks.onItemSelected(NAME);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    @Override
    protected void setSelectedSubdictItemId(int id) {
        mSelectedSubdictId = id + 1;
    }
}
