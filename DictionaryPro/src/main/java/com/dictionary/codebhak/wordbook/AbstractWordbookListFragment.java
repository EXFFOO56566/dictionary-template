package com.dictionary.codebhak.wordbook;

import android.database.Cursor;

import com.dictionary.codebhak.AbstractListFragment;
import com.dictionary.codebhak.data.appdata.AppDataContract;

/**
 * The basic class from which every wordbook list fragment inherits.
 */
// TODO: Simplify callback interface of this class's children now that we're getting the
// selected item's ID from the getSelectedWordbookId() method here.
// TODO: Make selectedId generic and move to superclass.
public abstract class AbstractWordbookListFragment extends AbstractListFragment {

    private static final int NO_SELECTION = -1;

    protected int mSelectedWordbookId = NO_SELECTION;

    /**
     * @return true if no list item is selected or false otherwise
     */
    public boolean nothingIsSelected() {
        return NO_SELECTION == mSelectedWordbookId;
    }

    /**
     * @return true if the selected word is in the favorites list or false otherwise
     */
    public boolean selectedWordIsFavorite() {
        String[] columns = new String[] {AppDataContract.WordbookFavorites._ID};
        String selection = AppDataContract.WordbookFavorites.COLUMN_NAME_WORDBOOK_ID + " = ?";
        String[] selectionArgs = new String[] {Integer.toString(mSelectedWordbookId)};
        Cursor cursor = getActivity().getContentResolver()
                .query(AppDataContract.WordbookFavorites.CONTENT_URI, columns, selection,
                        selectionArgs, null);
        boolean result = false;
        if (cursor.getCount() > 0) {
            result = true;
        }
        cursor.close();
        return result;
    }

    /**
     * @return the wordbook database ID of the selected item
     */
    public int getSelectedWordbookId() {
        return mSelectedWordbookId;
    }

    /**
     * Sets the selected item ID.
     * @param id the {@code ListView} position of the item to select
     */
    protected abstract void setSelectedWordbookItemId(int id);
}
