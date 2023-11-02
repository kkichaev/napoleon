package com.serviko.sales.main_views.price_filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.serviko.sales.R;
import com.serviko.sales.main_views.ChildFilterFragment;
import com.serviko.sales.main_views.Filter;

import java.util.List;
import java.util.Set;

public class Manufacturer extends ChildFilterFragment {
    @Override
    protected int getResourceId() {
        return R.layout.price_filter_mfr;
    }

    @Override
    protected Filter getFilter() { return model.priceFilter; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        PriceFilter filter = model.priceFilter;

        TableLayout table = v.findViewById(R.id.table);
        Set<String> mfr = model.getPartner().getValue().manufacturer;
        for(String m : mfr) {
            ViewGroup tr = (ViewGroup) View.inflate(getContext(), R.layout.order_filter_contract_row, null);
            TextView tv = tr.findViewById(R.id.tvName);
            tv.setText(m);

            ViewGroup.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            table.addView(tr, lp);

            ImageView iv = findIcon(tr);
            setImage(iv, filter.manufacturer.contains(m));
            tr.setOnClickListener(view -> {
                if(filter.manufacturer.contains(m)) {
                    filter.manufacturer.remove(m);
                    setImage(iv, false);
                } else {
                    filter.manufacturer.add(m);
                    setImage(iv, true);
                }
            });
        }

        return v;
    }
}
