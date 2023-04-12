package com.dictionary.codebhak.navigationdrawer;

import java.util.ArrayList;

/**
 * Created by ThangTB on 08/02/2015.
 */
public class NavigationDrawerCat extends AbstractNavigationDrawerItem {
    private static final int TYPE = 2;

    private ArrayList<String> mCats;

    public NavigationDrawerCat(int id, String label, ArrayList<String> cats) {
        super(id, label);
        this.mCats = cats;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isRow() {
        return false;
    }

    @Override
    public boolean isCat() {
        return true;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    public ArrayList<String> getCats() {
        return mCats;
    }

    public void setCats(ArrayList<String> mCats) {
        this.mCats = mCats;
    }
}
