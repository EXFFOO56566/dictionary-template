package com.dictionary.codebhak;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dictionary.codebhak.R;

/**
 * The basic activity from which all detail activities inherit. This class 
 * contains a single {@link AbstractDetailFragment} and is only used on phones.
 */
public abstract class AbstractDetailActivity extends ActionBarActivity {
    // TODO: Some code is repeated from MainActivity here. It would be good to
    // move this to a superclass or otherwise consolidate it somehow.
    /** Stores the mode title. */
    protected CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        
        // Set the status bar background color.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.green_accent_dark));
        }
        
        // Set the toolbar to act as the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_settings:
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        case R.id.action_help:
            displayHelp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Launches an email app that the user can use to send feedback about this app.
     */
    private void sendFeedback() {
        Uri uri = Uri.fromParts("mailto", getString(R.string.feedback_email), null);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
        Intent chooser = Intent.createChooser(intent, getString(R.string.feedback_intent_chooser));
        startActivity(chooser);
    }

    /**
     * A {@link DialogFragment} containing help text.
     */
    public static class HelpDialogFragment extends DialogFragment {
        
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.title_help);

            TextView textView = new TextView(getActivity());
            textView.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            textView.setPadding(25, 25, 25, 25);
            
            textView.setText(Html.fromHtml(getString(R.string.message_help)));
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            
            ScrollView scrollView = new ScrollView(getActivity());
            scrollView.addView(textView);
            builder.setView(scrollView);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            
            return builder.create();
        }
    }

    /**
     * Displays a dialog fragment containing help text.
     */
    private void displayHelp() {
        HelpDialogFragment dialogFragment = new HelpDialogFragment();
        dialogFragment.show(getFragmentManager(), "help");
    }

    /**
     * Sets the navigation bar navigation mode and title to the appropriate values.
     */
    protected abstract void restoreActionBar();

    // The following two methods are a workaround for a bug related to the appcompat-v7 library
    // on some LG devices. Thanks to Alex Lockwood for the fix: 
    // http://stackoverflow.com/questions/26833242/nullpointerexception-phonewindowonkeyuppanel1002-main

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_MENU == keyCode && Build.BRAND.equalsIgnoreCase("LGE")) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_MENU == keyCode && Build.BRAND.equalsIgnoreCase("LGE")) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
