package de.slg.schwarzes_brett;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.slg.leoapp.R;

class ExpandableListAdapter extends BaseExpandableListAdapter {

    private final Map<String, List<String>> eintraege;
    private final List<String> titel;
    private final LayoutInflater inflater;
    @Nullable
    private ArrayList<Integer> views;

    ExpandableListAdapter(LayoutInflater inflater, List<String> titel, Map<String, List<String>> eintraege) {
        this.inflater = inflater;
        this.eintraege = eintraege;
        this.titel = titel;
    }

    ExpandableListAdapter(LayoutInflater inflater, List<String> titel, Map<String, List<String>> eintraege, @Nullable ArrayList<Integer> views) {
        this.inflater = inflater;
        this.eintraege = eintraege;
        this.titel = titel;
        this.views = views;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item_expandable_title, null);

        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        textView.setText((String) getGroup(groupPosition));

        if(views != null) {
            TextView textViewViews = (TextView) convertView.findViewById(R.id.textViewViews);
            textViewViews.setVisibility(View.VISIBLE);
            String viewString = views.get(groupPosition) > 999 ? "999+" : String.valueOf(views.get(groupPosition));
            textViewViews.setText(viewString);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item_expandable_child, null);

        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        textView.setText((String) getChild(groupPosition, childPosition));
        return convertView;
    }

    @Override
    public int getGroupCount() {
        return titel.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return eintraege.get(titel.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return titel.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return eintraege.get(titel.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
