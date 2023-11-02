package com.ashberrysoft.leadertask.modern.fragment.intro_fragments;

/**
 * Created by Samsung on 25.12.2015.
 */


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.fragments.LTBaseFragment;


public class PreviewFragment7 extends LTBaseFragment {

    public static final String CLASS_PATH = PreviewFragment7.class.getSimpleName();
    public static PreviewFragment7 newInstance() {
        return new PreviewFragment7();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    // VIEW's
    private LinearLayout mContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        final View v = inflater.inflate(R.layout.fragment_intro, null);
        mContainer = (LinearLayout) v.findViewById(R.id.container7);
        mContainer.setVisibility(View.VISIBLE);

        return v;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }

    @Override
    public boolean showTitleBar() {
        return true;
    }

}