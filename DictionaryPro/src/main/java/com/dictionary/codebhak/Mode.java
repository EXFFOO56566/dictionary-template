package com.dictionary.codebhak;

/**
* An enum type to track the various app modes accessible from the navigation drawer.
*/
public enum Mode {
    CAT(0, "category"),
    WORDBOOK_BROWSE(1, "wordbook_browse"),
    WORDBOOK_FAVORITES(2, "wordbook_favorites"),
    WORDBOOK_HISTORY(3, "wordbook_history"),
    SYNTAX_BROWSE(5, "subdict_browse"),
    SYNTAX_BOOKMARKS(6, "subdict_bookmarks");

    private final int mPosition;
    private final String mName;

    /**
     * Enum constructor.
     * @param position the navigation drawer position corresponding to this mode
     * @param name the name of this mode
     */
    Mode(int position, String name) {
        mPosition = position;
        mName = name;
    }

    @Override
    public String toString() {
        return mName;
    }

    /**
     * Returns the name of this mode.
     * <p>
     * This is just a duplicate of toString() for now, but it's included here in case we ever
     * want to make toString() return something other than mName.
     */
    public String getName() {
        return mName;
    }

    public int getPosition() {
        return mPosition;
    }

    /**
     * Returns the {@code Mode} corresponding to the specified navigation drawer position.
     * @param  position the navigation drawer position for which to search
     * @return the {@code Mode} corresponding to the specified position
     */
    public static Mode getModeFromPosition(int position) {
        for (Mode m : Mode.values()) {
            if (m.mPosition == position) {
                return m;
            }
        }
        throw new IllegalArgumentException("Invalid nav drawer position");
    }

    /**
     * Returns the {@code Mode} corresponding to the specified name.
     * @param  name the name for which to search
     * @return the {@code Mode} corresponding to the specified name
     */
    public static Mode getModeFromName(String name) {
        for (Mode m : Mode.values()) {
            if (m.mName.equals(name)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Invalid name");
    }

    /**
     * Returns true if this is a wordbook mode.
     * @return true if this is a wordbook mode, or false otherwise
     */
    public boolean isWordbookMode() {
        return this.equals(WORDBOOK_BROWSE)
                || this.equals(WORDBOOK_FAVORITES)
                || this.equals(WORDBOOK_HISTORY);
    }

    /**
     * Returns trueif this is a subdict mode.
     * @return trueif this is a subdict mode, or false otherwise
     */
    public boolean isSubdictMode() {
        return this.equals(SYNTAX_BROWSE) || this.equals(SYNTAX_BOOKMARKS);
    }

    public boolean isCateMode() {
        return this.equals(CAT);
    }
}
