package com.dictionary.codebhak.navigationdrawer;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dictionary.codebhak.R;

import java.util.ArrayList;

/**
 * Based on the tutorial at http://www.michenux.net/android-navigation-drawer-748.html.
 */
public class NavigationDrawerAdapter extends ArrayAdapter<AbstractNavigationDrawerItem> {

    private final LayoutInflater mInflater;
    private final Context mContext;

    public NavigationDrawerAdapter(Context context, int id, AbstractNavigationDrawerItem[] objects) {
        super(context, id, objects);
        this.mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        AbstractNavigationDrawerItem item = this.getItem(position);
        if (item.isRow()) {
            view = getRowView(convertView, parent, item, position);
        }else if (item.isCat()){
            view = getViewCat(convertView, parent, item, position);
        }
        else {
            view = getHeadingView(convertView, parent, item, position);
        }
        return view;
    }

    public View getRowView(View convertView, ViewGroup parentView, 
            AbstractNavigationDrawerItem item, int position) {
        NavigationDrawerRowHolder navDrawerRowHolder = null;

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.navigation_drawer_row, parentView, false);
            TextView textView = 
                    (TextView) convertView.findViewById(R.id.navigation_drawer_row_text);
            ImageView imageView = 
                    (ImageView) convertView.findViewById(R.id.navigation_drawer_row_icon);

            navDrawerRowHolder = new NavigationDrawerRowHolder();
            navDrawerRowHolder.textView = textView;
            navDrawerRowHolder.imageView = imageView;

            convertView.setTag(navDrawerRowHolder);
        }

        if (null == navDrawerRowHolder) {
            navDrawerRowHolder =(NavigationDrawerRowHolder) convertView.getTag();
        }

        navDrawerRowHolder.textView.setText(item.getLabel());

        // Text and icon color are determined by whether the item is checked.
        if (((ListView) parentView).isItemChecked(position)) {
            // Item is highlighted.
            ((NavigationDrawerRow) item).setIconHighlighted(true);
            TextView textView = navDrawerRowHolder.textView;
            Resources resources = mContext.getResources();
            int textDark = resources.getColor(R.color.primary_dark_material_dark);
            int backgroundLight = resources.getColor(R.color.background_material_light);
            textView.setTextColor(textDark);
            convertView.setBackgroundColor(backgroundLight);
        } else {
            // Item is not highlighted.
            ((NavigationDrawerRow) item).setIconHighlighted(false);
            TextView textView = navDrawerRowHolder.textView;
            int textDark = mContext.getResources().getColor(R.color.primary_dark_material_dark);
            int backgroundWhite = mContext.getResources().getColor(R.color.background_white);
            textView.setTextColor(textDark);
            convertView.setBackgroundColor(backgroundWhite);
        }

        navDrawerRowHolder.imageView.setImageResource(((NavigationDrawerRow) item).getIcon());

        return convertView;
    }

    public View getHeadingView(View convertView, ViewGroup parentView, 
            AbstractNavigationDrawerItem item, int position) {
        NavigationDrawerHeadingHolder holder = null;
        TextView textView;

        if (null == convertView) {
            if (0 == position) {
                convertView = mInflater.inflate(R.layout.navigation_drawer_heading_first,
                        parentView, false);
            } else {
                convertView = mInflater.inflate(R.layout.navigation_drawer_heading,
                        parentView, false);
            }
            textView = (TextView) convertView.findViewById(R.id.navigation_drawer_heading_text);

            holder = new NavigationDrawerHeadingHolder();
            holder.textView = textView;
            convertView.setTag(holder);
        }

        if (null == holder) {
            holder = (NavigationDrawerHeadingHolder) convertView.getTag();
        }

        holder.textView.setText(item.getLabel());

        return convertView;
    }

    public View getViewCat(View convertView, ViewGroup parentView,
                               AbstractNavigationDrawerItem item, int position) {
        NavigationDrawerCategoryHolder holder = null;
        Spinner spinner;

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.navigation_drawer_cat,parentView, false);
            spinner = (Spinner) convertView.findViewById(R.id.spinner_cat);

            holder = new NavigationDrawerCategoryHolder();
            holder.spinCat = spinner;
            convertView.setTag(holder);
        }

        if (null == holder) {
            holder = (NavigationDrawerCategoryHolder) convertView.getTag();
        }

        final ArrayList<String> cats = ((NavigationDrawerCat)item).getCats();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item,cats);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        holder.spinCat.setAdapter(spinnerArrayAdapter);
        holder.spinCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (onCategoryChange!=null)
                    onCategoryChange.onChange(cats.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (onCategoryChange!=null)
                    onCategoryChange.onNothing();
            }
        });

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return this.getItem(position).getType();
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position).isEnabled();
    }

    private static class NavigationDrawerRowHolder {
        private TextView textView;
        private ImageView imageView;
    }

    private static class NavigationDrawerHeadingHolder {
        private TextView textView;
    }

    private static class NavigationDrawerCategoryHolder {
        private Spinner spinCat;
    }
    OnCategoryChange onCategoryChange;

    public void setOnCategoryChange(OnCategoryChange onCategoryChange) {
        this.onCategoryChange = onCategoryChange;
    }

    public interface OnCategoryChange{
        public void onChange(String s);
        public void onNothing();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
