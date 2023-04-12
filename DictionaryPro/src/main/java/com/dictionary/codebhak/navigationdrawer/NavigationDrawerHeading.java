package com.dictionary.codebhak.navigationdrawer;

/**
 * Based on the tutorial at http://www.michenux.net/android-navigation-drawer-748.html.
 */
public class NavigationDrawerHeading extends AbstractNavigationDrawerItem {

    private static final int TYPE = 0;
    
    public NavigationDrawerHeading(int id, String label) {
        super(id, label);
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
        return false;
    }

    @Override
    public int getType() {
        return TYPE;
    }
}
