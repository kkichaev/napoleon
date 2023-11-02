package com.grsoft.napoleon.main;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.DaData;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends ArrayAdapter<String> {
    List<String> src;

    public AddressAdapter(Context context) {
        super(context, R.layout.setting_list_item);
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
