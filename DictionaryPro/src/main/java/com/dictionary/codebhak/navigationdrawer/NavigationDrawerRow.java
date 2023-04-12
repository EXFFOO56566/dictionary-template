package com.dictionary.codebhak.navigationdrawer;

import android.content.Context;

/**
 * Based on the tutorial at http://www.michenux.net/android-navigation-drawer-748.html.
 */
public class NavigationDrawerRow extends AbstractNavigationDrawerItem {

    private static final int TYPE = 1;

    private final int mIconUnhighlighted;
    private final int mIconHighlighted;
    
    private int mCurrentIcon;

    public NavigationDrawerRow(int id, String label, int iconUnhighlighted,
            int iconHighlighted, Context context) {
        super(id, label);
        mIconUnhighlighted = iconUnhighlighted;
        mIconHighlighted = iconHighlighted;
        mCurrentIcon = mIconUnhighlighted;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isRow() {
        return true;
    }

    @Override
    public boolean isCat() {
        return false;
    }

    public int getIcon() {
        return mCurrentIcon;
    }

    public void setIconHighlighted(boolean highlighted) {
        if (highlighted) {
            mCurrentIcon = mIconHighlighted;
        } else {
            mCurrentIcon = mIconUnhighlighted;
        }
    }

    @Override
    public int getType() {
        return TYPE;
    }
}
