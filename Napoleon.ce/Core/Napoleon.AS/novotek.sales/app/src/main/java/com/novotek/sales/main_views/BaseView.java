package com.novotek.sales.main_views;

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

import com.novotek.sales.R;
import com.novotek.utils.ImageGetController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseView extends Fragment {
    Model model;
    ImageGetController images = new ImageGetController();

    protected abstract int getResourceId();
    public abstract String getFragmentTag();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(getActivity()).get(Model.class);

        model.getPicEvent().observe(getViewLifecycleOwner(), event -> images.update());

        View v = inflater.inflate(getResourceId(), container, false);
        return v;
    }
}
