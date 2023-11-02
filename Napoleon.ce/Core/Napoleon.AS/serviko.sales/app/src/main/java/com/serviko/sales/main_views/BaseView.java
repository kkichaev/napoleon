package com.serviko.sales.main_views;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.serviko.sales.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseView extends Fragment {
    Model model;
    Map<String, ImageView> images = new HashMap<>();

    abstract int getResourceId();
    public abstract String getFragmentTag();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(getActivity()).get(Model.class);

        model.getPicEvent().observe(getViewLifecycleOwner(), event -> {
            List<String> rmv = new ArrayList<>();
            for(Map.Entry<String, ImageView> kv : images.entrySet()) {
                Bitmap b = model.getPhoto(kv.getKey());
                if(b != null) {
                    kv.getValue().setImageBitmap(b);
                    kv.getValue().setVisibility(View.VISIBLE);
                    rmv.add(kv.getKey());
                }
            }
            for(String key : rmv)
                images.remove(key);
        });

        View v = inflater.inflate(getResourceId(), container, false);
        return v;
    }

    protected void requestImage(String url, ImageView imageView) {
        for(Map.Entry<String, ImageView> kv : images.entrySet()) {
            if(kv.getValue() == imageView) {
                images.remove(kv.getKey());
                break;
            }
        }
        images.put(url, imageView);
        imageView.setImageResource(R.drawable.coming_soon);
    }
}
