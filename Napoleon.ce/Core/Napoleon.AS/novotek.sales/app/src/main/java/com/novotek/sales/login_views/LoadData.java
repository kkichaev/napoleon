package com.novotek.sales.login_views;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.novotek.sales.R;
import com.novotek.sales.main_views.BaseView;

public class LoadData extends BaseView {

    static int IMAGE_DELAY = 2000;

    Model model;

    @Override
    protected int getResourceId() {
        return R.layout.load_data_frame;
    }

    @Override
    public String getFragmentTag() {
        return "LoadData";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(getActivity()).get(Model.class);
        model.load(getContext());

        View v =  inflater.inflate(R.layout.load_data_frame, container, false);

        ImageView iv = v.findViewById(R.id.loading);
        Animation a = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        a.setDuration(6000);
        a.setRepeatCount(Animation.INFINITE);
        a.setInterpolator(new LinearInterpolator(getContext(), null));

        iv.startAnimation(a);

        model.loadData(getActivity());
        return v;
    }
}
