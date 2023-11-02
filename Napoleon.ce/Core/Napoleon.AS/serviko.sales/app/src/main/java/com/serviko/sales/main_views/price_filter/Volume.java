package com.serviko.sales.main_views.price_filter;

import android.util.Pair;

import com.serviko.sales.R;
import com.serviko.sales.main_views.ChildFilterFragment;
import com.serviko.sales.main_views.Filter;

public class Volume extends ChildFilterFragment {
    @Override
    protected int getResourceId() {
        return R.layout.price_filter_volume;
    }

    @Override
    protected Filter getFilter() { return model.priceFilter; }

    @Override
    protected Pair<Integer, String>[] bindings() {
        return new Pair[] {
                new Pair<Integer, String>(R.id.trV01, "v01"),
                new Pair<Integer, String>(R.id.trV25, "v25"),
                new Pair<Integer, String>(R.id.trV33, "v33"),
                new Pair<Integer, String>(R.id.trV5, "v5"),
                new Pair<Integer, String>(R.id.trV7, "v7"),
                new Pair<Integer, String>(R.id.trV1, "v1"),
                new Pair<Integer, String>(R.id.trV15, "v15"),
                new Pair<Integer, String>(R.id.trV2, "v2"),
                new Pair<Integer, String>(R.id.trV29, "v29"),
        };
    }
}
