package com.serviko.sales.main_views.order_filter;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.serviko.sales.R;

public class State extends OrderChildFilter {
    @Override
    protected int getResourceId() { return R.layout.order_filter_state; }

    @Override
    protected Pair<Integer, String>[] bindings() {
        return new Pair[] {
                new Pair<Integer, String>(R.id.trActive, "active"),
                new Pair<Integer, String>(R.id.trDone, "done"),
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }
}
