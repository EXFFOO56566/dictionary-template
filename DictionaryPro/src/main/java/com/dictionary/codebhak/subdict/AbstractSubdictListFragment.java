package com.dictionary.codebhak.subdict;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.dictionary.codebhak.AbstractListFragment;
import com.dictionary.codebhak.data.appdata.AppDataContract;

/**
 * The basic class from which every subdict list fragment inherits.
 */
public abstract class AbstractSubdictListFragment extends AbstractListFragment {
    
    // TODO: Simplify callback interface of this class's children now that we're getting the
    // selected item's ID from the getSelectedWordbookId() method here.
    
    private static final int NO_SELECTION = -1;
    
    protected int mSelectedSubdictId = NO_SELECTION;

    /**
     * @return true if no list item is selected or false otherwise
     */
    public boolean nothingIsSelected() {
        return NO_SELECTION == mSelectedSubdictId;
    }

    /**
     * @return true if the selected word is in the bookmarks list or false otherwise
     */
    public boolean selectedSectionIsBookmarked() {
        String[] columns = new String[] {AppDataContract.SubdictBookmarks._ID};
        String selection = AppDataContract.SubdictBookmarks.COLUMN_NAME_SYNTAX_ID + " = ?";
        String[] selectionArgs = new String[] {Integer.toString(mSelectedSubdictId)};
        ContentResolver resolver = getActivity().getContentResolver();
        Uri uri = AppDataContract.SubdictBookmarks.CONTENT_URI;
        Cursor cursor = resolver.query(uri, columns, selection, selectionArgs, null);
        boolean result = false;
        if (cursor.getCount() > 0) {
            result = true;
        }
        cursor.close();
        return result;
    }

    /**
     * @return the subdict database ID of the selected item
     */
    public int getSelectedSubdictId() { return mSelectedSubdictId; }

    /**
     * Sets the selected item ID.
     * @param id the {@code ListView} position of the item to select
     */
    protected abstract void setSelectedSubdictItemId(int id);
}
