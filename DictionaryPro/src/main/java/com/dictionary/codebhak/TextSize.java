package com.dictionary.codebhak;

/**
 * An enum type used to store text size values.
 * <p>
 * Note that the scaled pixel values stored here correspond to the standard Android
 * TextAppearance values defined in the SDK data/res/values/styles.xml file.
 */
enum TextSize {
    SMALL("Small", 14),
    MEDIUM("Medium", 18),
    LARGE("Large", 22);

    private final String mName; // Size name stored in preference array
    private final float mSize;  // Text size in scaled pixels

    /**
     * Enum type constructor.
     * @param name the name of the size defined in the preferences array
     * @param size the text size in scaled pixels
     */
    TextSize(String name, float size) {
        mName = name;
        mSize = size;
    }

    /**
     * Returns the text size in scaled pixels for the specified size.
     * @param name the name of the size defined in the preferences array
     * @return the corresponding text size in scaled pixels
     */
    public static float getScaledPixelSize(String name) {
        for (TextSize tx : TextSize.values()) {
            if (name.equals(tx.mName)) {
                return tx.mSize;
            }
        }
        throw new IllegalArgumentException("Invalid text size name.");
    }
}
