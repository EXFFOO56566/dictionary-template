package com.dictionary.codebhak.navigationdrawer;

/**
 * Based on the tutorial at http://www.michenux.net/android-navigation-drawer-748.html.
 */
public abstract class AbstractNavigationDrawerItem {

    private int mId;
    private String mLabel;

    public AbstractNavigationDrawerItem(int id, String label) {
        mId = id;
        mLabel = label;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String mLabel) {
        this.mLabel = mLabel;
    }

    public abstract boolean isEnabled();
    public abstract boolean isRow();
    public abstract boolean isCat();
    public abstract int getType();
}
