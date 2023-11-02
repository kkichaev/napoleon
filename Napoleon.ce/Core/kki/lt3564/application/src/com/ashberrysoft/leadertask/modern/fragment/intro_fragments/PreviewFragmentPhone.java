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
import android.widget.LinearLayout;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.fragments.LTBaseFragment;


public class PreviewFragmentPhone extends LTBaseFragment {

    public static final String CLASS_PATH = PreviewFragmentPhone.class.getSimpleName();
    public static PreviewFragmentPhone newInstance() {
        return new PreviewFragmentPhone();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    // VIEW's
    private LinearLayout mContainer;
    public static EditText mEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        final View v = inflater.inflate(R.layout.fragment_intro, null);
        mContainer = (LinearLayout) v.findViewById(R.id.container_phone);
        mEditText = (EditText) v.findViewById(R.id.container_edittext__phone);
        mContainer.setVisibility(View.VISIBLE);

        return v;
    }

    public String getPhone() {
        return mEditText.getText().toString().trim();
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