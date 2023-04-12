package com.dictionary.codebhak.subdict;

import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dictionary.codebhak.AbstractDetailFragment;
import com.dictionary.codebhak.LangTextView;
import com.dictionary.codebhak.R;
import com.dictionary.codebhak.data.appdata.AppDataContract;
import com.dictionary.codebhak.data.subdict.SubdictContract;
import com.dictionary.codebhak.data.subdict.SubdictSection;
import com.dictionary.codebhak.data.subdict.SubdictXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A {@link com.dictionary.codebhak.AbstractDetailFragment} used to display a subdict section.
 */
public class SubdictDetailFragment extends AbstractDetailFragment {
    
    public static final String TAG = "SubdictDetailFragment";
    public static final String ARG_XML = "xml";
    
//    private SubdictSection mSection;
    private boolean mBlank = false;
String strHtml="";
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SubdictDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_XML)) {
            String xml = getArguments().getString(ARG_XML);
            strHtml = xml;
//            SubdictXmlParser parser = new SubdictXmlParser();
//            InputStream in = new ByteArrayInputStream(xml.getBytes());
//            try {
//                mSection = parser.parse(in);
//            } catch (XmlPullParserException | IOException e) {
//                Log.e(TAG, Log.getStackTraceString(e));
//            }
        } else {
            mBlank = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        if (!mBlank) {
            // We add the header here since we can't access resources from a static context in
            // the SubdictSection class.
            Spanned html = Html.fromHtml(strHtml + getString(R.string.subdict_footer));
            LangTextView textView = (LangTextView) rootView.findViewById(R.id.item_detail);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(html); // Replace with parsed data.
        }

        return rootView;
    }

    /**
     * Returns the section title corresponding to the specified subdict ID.
     * @param id the subdict ID for which to search
     * @return the corresponding section title
     */
    private String getSectionFromSubdictId(int id) {
        String[] projection = {SubdictContract.COLUMN_NAME_SECTION};
        String selection = SubdictContract._ID + " = ?";
        String[] selectionArgs = {Integer.toString(id)};
        ContentResolver resolver = getActivity().getContentResolver();
        Uri uri = SubdictContract.CONTENT_URI;
        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null);
        String section;
        if (cursor.moveToFirst()) {
            section = cursor.getString(0);
        } else {
            throw new IllegalArgumentException("Invalid subdict ID: " + id);
        }
        return section;
    }

    /**
     * Adds the specified word to the wordbook favorites list.
     * @param subdictId the subdict ID of the section
     * @param section the section title to add
     */
    protected void addSubdictBookmark(int subdictId, String section) {
        ContentValues values = new ContentValues();
        values.put(AppDataContract.SubdictBookmarks.COLUMN_NAME_SYNTAX_ID, subdictId);
        values.put(AppDataContract.SubdictBookmarks.COLUMN_NAME_SYNTAX_SECTION, section);
        Uri uri = AppDataContract.SubdictBookmarks.CONTENT_URI;
        getActivity().getContentResolver().insert(uri, values);
        getActivity().invalidateOptionsMenu();
        displayToast(getString(R.string.toast_bookmark_added));
    }

    /**
     * Removes the specified section from the subdict bookmarks list.
     * @param subdictId the subdict ID of the section to remove
     */
    protected void removeSubdictBookmark(int subdictId) {
        String selection = AppDataContract.SubdictBookmarks.COLUMN_NAME_SYNTAX_ID + " = ?";
        String[] selectionArgs = {Integer.toString(subdictId)};
        ContentResolver resolver = getActivity().getContentResolver();
        resolver.delete(AppDataContract.SubdictBookmarks.CONTENT_URI, selection, selectionArgs);
        getActivity().invalidateOptionsMenu();
        displayToast(getString(R.string.toast_bookmark_removed));
    }

    // The following two methods should only be used in two-pane mode.
    // TODO: Throw exception if these methods are called in one-pane mode.

    /**
     * Adds the currently selected section to the subdict bookmarks list.
     */
    public void addSubdictBookmark() {
        FragmentManager mgr = getActivity().getFragmentManager();
        AbstractSubdictListFragment fragment = 
                (AbstractSubdictListFragment) mgr.findFragmentById(R.id.item_list_container);
        int subdictId = fragment.getSelectedSubdictId();
        String section = getSectionFromSubdictId(subdictId);
        addSubdictBookmark(subdictId, section);
    }

    /**
     * Removes the currently selected section from the subdict bookmarks list.
     */
    public void removeSubdictBookmark() {
        FragmentManager mgr = getActivity().getFragmentManager();
        AbstractSubdictListFragment fragment = 
                (AbstractSubdictListFragment) mgr.findFragmentById(R.id.item_list_container);
        int subdictId = fragment.getSelectedSubdictId();
        removeSubdictBookmark(subdictId);
    }
}
