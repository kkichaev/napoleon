package com.grsoft.dlc;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class ApplicationsAdapter extends ArrayAdapter<ApplicationInfo> {

    public ApplicationsAdapter(Context context, ArrayList<ApplicationInfo> apps) {
        super(context, 0, apps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ApplicationInfo info = getItem(position);

        if (convertView == null) {
            final LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.application, parent, false);
        }

        Drawable icon = info.icon;

        final TextView textView = (TextView) convertView.findViewById(R.id.label);
        textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
        textView.setText(info.title);

        return convertView;
    }
}