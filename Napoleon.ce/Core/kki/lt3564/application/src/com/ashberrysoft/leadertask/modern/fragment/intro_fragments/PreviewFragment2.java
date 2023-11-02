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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.fragments.LTBaseFragment;
import com.ashberrysoft.leadertask.modern.fragment.MenuFragment;


public class PreviewFragment2 extends LTBaseFragment {

    public static final String CLASS_PATH = PreviewFragment2.class.getSimpleName();
    public static PreviewFragment2 newInstance() {
        return new PreviewFragment2();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    // VIEW's
    private LinearLayout mContainer;
    private static CheckBox mCheckBox1;
    private static CheckBox mCheckBox2;
    private static CheckBox mCheckBox3;
    private static CheckBox mCheckBox4;
    private static CheckBox mCheckBox5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        final View v = inflater.inflate(R.layout.fragment_intro, null);
        mContainer = (LinearLayout) v.findViewById(R.id.container2);
        mCheckBox1 = (CheckBox) v.findViewById(R.id.container2_checkbox1);
        mCheckBox2 = (CheckBox) v.findViewById(R.id.container2_checkbox2);
        mCheckBox3 = (CheckBox) v.findViewById(R.id.container2_checkbox3);
        mCheckBox4 = (CheckBox) v.findViewById(R.id.container2_checkbox4);
        mCheckBox5 = (CheckBox) v.findViewById(R.id.container2_checkbox5);
        mContainer.setVisibility(View.VISIBLE);


        return v;
    }

    public boolean[] getAllCheckBoxes() {
        boolean[] allCheckBoxes = new boolean[]{mCheckBox1.isChecked(),mCheckBox2.isChecked(),mCheckBox3.isChecked(),mCheckBox4.isChecked(),mCheckBox5.isChecked()};
        return allCheckBoxes;
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