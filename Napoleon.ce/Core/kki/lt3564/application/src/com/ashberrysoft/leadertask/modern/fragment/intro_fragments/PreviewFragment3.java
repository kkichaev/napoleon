package com.ashberrysoft.leadertask.modern.fragment.intro_fragments;

/**
 * Created by Samsung on 25.12.2015.
 */


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.fragments.LTBaseFragment;
import com.ashberrysoft.leadertask.modern.activity.PreviewActivity;


public class PreviewFragment3 extends LTBaseFragment {

    public static final String CLASS_PATH = PreviewFragment3.class.getSimpleName();
    public static PreviewFragment3 newInstance() {
        return new PreviewFragment3();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    // VIEW's
    private LinearLayout mContainer;
    public static EditText mEditText1;
    public static EditText mEditText2;
    public static EditText mEditText3;
    public static EditText mEditText4;
    private static TextView mIAmAlone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        final View v = inflater.inflate(R.layout.fragment_intro, null);
        mContainer = (LinearLayout) v.findViewById(R.id.container3);
        mEditText1 = (EditText) v.findViewById(R.id.container3edittext1);
        mEditText2 = (EditText) v.findViewById(R.id.container3edittext2);
        mEditText3 = (EditText) v.findViewById(R.id.container3edittext3);
        mEditText4 = (EditText) v.findViewById(R.id.container3edittext4);
        mIAmAlone = (TextView) v.findViewById(R.id.container3havenotemps);
        mIAmAlone.setClickable(true);
        mIAmAlone.setPaintFlags(mIAmAlone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mIAmAlone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText1.setText("");
                mEditText2.setText("");
                mEditText3.setText("");
                mEditText4.setText("");
                ((PreviewActivity) getActivity()).goToSixSlide2();
            }
        });
        mContainer.setVisibility(View.VISIBLE);

        return v;
    }

    /*public String[] getAllAddedUsers() {
        String[] allCheckBoxes = new String[]{};
        try {
            allCheckBoxes = new String[]{mEditText1.getText().toString().trim(), mEditText2.getText().toString().trim(), mEditText3.getText().toString().trim(), mEditText4.getText().toString().trim()};
        } finally {
            return allCheckBoxes;
        }
    }*/

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