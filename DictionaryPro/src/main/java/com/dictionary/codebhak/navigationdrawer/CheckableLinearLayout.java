package com.dictionary.codebhak.navigationdrawer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * This is a custom LinearLayout that allows items to be selected and highlighted.
 * <p>
 * This class is based on advice gleaned from the following sources:
 * <ul>
 *     <li>http://tokudu.com/post/50023900640/android-checkable-linear-layout</li>
 *     <li>http://stackoverflow.com/questions/8369640/listview-setitemchecked-only-works-with-standard-arrayadapter-does-not-work-w</li>
 * </ul>
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {

    private boolean mIsChecked = false;

    public CheckableLinearLayout(Context context) {
        super(context);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setChecked(boolean b) {
        mIsChecked = b;
    }

    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void toggle() {
        mIsChecked = !mIsChecked;
    }
}
