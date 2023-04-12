package com.dictionary.codebhak;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dictionary.codebhak.R;

/**
 * The basic class from which every detail fragment inherits.
 */
public abstract class AbstractDetailFragment extends Fragment {
    
    private static final String KEY_SCROLL_Y = "scroll_y";

    // We use a single Toast object to prevent overlapping toasts when the user
    // repeatedly taps an icon that displays a toast.
    protected Toast mToast;

    @SuppressLint("ShowToast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToast = Toast.makeText(getActivity(), null, Toast.LENGTH_SHORT);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SCROLL_Y)) {
            int scrollY = savedInstanceState.getInt(KEY_SCROLL_Y);
            View scrollView = getActivity().findViewById(R.id.detail_scroll_view);
            scrollView.setScrollY(scrollY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        View scrollView = getActivity().findViewById(R.id.detail_scroll_view);
        if (scrollView != null) {
            int scrollY = scrollView.getScrollY();
            outState.putInt(KEY_SCROLL_Y, scrollY);
        }
    }

    /**
     * Displays a toast containing the specified text.
     * <p>
     * All children of this class should display toasts only by calling this
     * method in order to prevent creating overlapping toasts.
     * @param message the text to display in the toast
     */
    protected void displayToast(String message) {
        mToast.setText(message);
        mToast.show();
    }
}
