package com.novotek.utils;

import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import com.grsoft.camera.CameraActivity;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;
import com.novotek.sales.main_views.Model;

public class FindGoodsController {
    Model model;
    MainActivity activity;

    public interface Events {
        boolean querySubmit(String text);
    }

    public FindGoodsController(View v, MainActivity activity, Events handler) {
        this.activity = activity;
        model = new ViewModelProvider(activity).get(Model.class);

        SearchView find = v.findViewById(R.id.find);
        find.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return handler.querySubmit(query);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        View bc = v.findViewById(R.id.barcode);
        bc.setOnClickListener(view -> {
            CameraActivity.openBCScanner(activity);
        });
    }
}
