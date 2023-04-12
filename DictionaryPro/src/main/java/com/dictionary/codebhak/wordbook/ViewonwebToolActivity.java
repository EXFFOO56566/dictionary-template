package com.dictionary.codebhak.wordbook;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dictionary.codebhak.R;
import com.dictionary.codebhak.SettingsActivity;

/**
 * Displays the Viewonweb Lang Word Study Tool page in a {@code WebView}. This class adds a style
 * element to each page in order to properly display Lang characters using the Noto Serif font.
 */
public class ViewonwebToolActivity extends ActionBarActivity {

    private static final String URL_START = "http://www.thefreedictionary.com/";

    private WebView mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewonweb_tool);
        
        // Set the status bar background color.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.green_accent_dark));
        }
        
        // Set the toolbar to act as the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.title_wordbook));
        actionBar.setSubtitle(getString(R.string.subtitle_viewonweb_tool));

        // Create the URL to retrieve.
        Intent intent = getIntent();
        String morph = intent.getStringExtra(WordbookDetailFragment.VIEWONWEB_TOOL_EXTRA_KEY);
        String url = URL_START + morph + "#content";

        // Display a progress bar.
        mWebView = (WebView) findViewById(R.id.viewonweb_tool_webview);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        final ActionBarActivity activity = this;
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (100 == progress) {
                    activity.setSupportProgressBarVisibility(false);
                }
                activity.setProgress(progress * 1000);
            }
        });

        // We inject a custom style element into each page here in order to load a local typeface.
        // The WebView security features prevent us from loading a local CSS file instead.
        // We don't do anything to cache the font here; I don't know if that's possible.
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:(function(){var style=document.createElement('style');"
                        + "style.innerHTML='<style>@font-face{font-family:NotoSerif;"
                        + "src: url(\"fonts/NotoSerif-Regular.ttf\");}.lang{font-family:"
                        + "NotoSerif, Gentium, Cardo, serif;}</style>';"
                        + "document.getElementsByTagName('head')[0].appendChild(style);})();");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                    String failingUrl) {
                Toast.makeText(activity, getString(R.string.webview_error) + description,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Enable pinch-to-zoom.
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        mWebView.loadUrl(url);
    }

    
    // Workaround for a bug related to the appcompat-v7 library on some LG devices. Thanks to 
    // Alex Lockwood for the fix: http://stackoverflow.com/questions/26833242/nullpointerexception-phonewindowonkeyuppanel1002-main
    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (KeyEvent.KEYCODE_MENU == keyCode && Build.BRAND.equalsIgnoreCase("LGE")) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }    
    
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        // Workaround for a bug related to the appcompat-v7 library on some LG devices. See comment
        // above onKeyUp method.
        if (KeyEvent.KEYCODE_MENU == keyCode && Build.BRAND.equalsIgnoreCase("LGE")) {
            return true;
        }
        
        // Use the back button to navigate through the web history if the user
        // has clicked any links.
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: Move all of the common global options menu code below to a superclass.

    /**
     * Launches an email app that the user can use to send feedback about this app.
     */
    private void sendFeedback() {
        Intent intent = new Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", getString(R.string.feedback_email), null));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
        startActivity(Intent.createChooser(intent, getString(R.string.feedback_intent_chooser)));
    }

    /**
     * A {@link android.app.DialogFragment} containing help text.
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

   
}
