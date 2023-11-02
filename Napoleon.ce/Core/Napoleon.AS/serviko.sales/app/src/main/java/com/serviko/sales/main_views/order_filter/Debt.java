package com.serviko.sales.main_views.order_filter;

import android.util.Pair;

import com.serviko.sales.R;

public class Debt extends OrderChildFilter {
    @Override
    protected int getResourceId() {
        return R.layout.order_filter_debt;
    }

    @Override
    protected Pair<Integer, String>[] bindings() {
        return new Pair[] {
                new Pair<Integer, String>(R.id.trPayed, "payed"),
                new Pair<Integer, String>(R.id.trDebt, "unpayed"),
        };
    }
}
