package com.novotek.sales.main_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.novotek.sales.MainActivity;
import com.novotek.sales.R;

import java.util.ArrayList;

public class FavPrice extends Price {
    public FavPrice(ArrayList<String> src, String title) {
        super(src, title);
    }

    @Override
    protected int getResourceId() {
        return R.layout.fav_view;
    }

    @Override
    protected boolean useFilter() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        if(src.size() == 0) {
            v.findViewById(R.id.llEmpty).setVisibility(View.VISIBLE);
            v.findViewById(R.id.price).setOnClickListener(view ->
                ((MainActivity)getActivity()).openItem(R.id.itCatalog)
            );
            v.findViewById(R.id.lvItems).setVisibility(View.GONE);
        }
        return v;
    }
}
