package com.grsoft.napoleon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends ArrayAdapter<String> {
    List<String> src;

    public AddressAdapter(Context context) {
        super(context, R.layout.address_suggest);
        src = new ArrayList<>();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return src.get(position);
    }

    @Override
    public int getCount() {
        return src.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if(view == null) {
            view = View.inflate(getContext(), R.layout.address_suggest, null);
        }
        String el = getItem(position);
        ((TextView)view.findViewById(R.id.text)).setText(el);
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults res = new FilterResults();
            res.values = new ArrayList<String>();
            if (constraint.length() > 0) {
                List<String> adr = DaData.getAddresses(constraint.toString());
                res.values = adr;
                res.count = adr.size();
            }
            return res;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<String> cd = (List<String>) results.values;
            if (cd == null) {
                src = new ArrayList<>();
                notifyDataSetInvalidated();
                return;
            }
            src = cd;
            if (cd.size() > 0)
                notifyDataSetChanged();
            else
                notifyDataSetInvalidated();
        }
    };
}
