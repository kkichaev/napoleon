package com.ashberrysoft.leadertask.modern.fragment.intro_fragments;

/**
 * Created by Samsung on 25.12.2015.
 */


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.fragments.LTBaseFragment;
import com.ashberrysoft.leadertask.modern.activity.PreviewActivity;
import com.ashberrysoft.leadertask.utils.Utils;

import java.util.ArrayList;

import static com.ashberrysoft.leadertask.R.id.parent;


public class PreviewFragment4 extends LTBaseFragment {

    public static final String CLASS_PATH = PreviewFragment4.class.getSimpleName();
    public static PreviewFragment4 newInstance() {
        return new PreviewFragment4();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    // VIEW's
    private LinearLayout mContainer;
    public static EditText mEditText;
    private static Spinner mSpinner;
    private static ArrayAdapter<String> mAdapter;
    private static int mPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        final View v = inflater.inflate(R.layout.fragment_intro, null);
        mContainer = (LinearLayout) v.findViewById(R.id.container4);
        mEditText = (EditText) v.findViewById(R.id.container4edittext);
        mSpinner = (Spinner) v.findViewById(R.id.container4spinner);
        mContainer.setVisibility(View.VISIBLE);

        ArrayList <String> arrayList = new ArrayList<>();
        arrayList.add(getString(R.string.preview_slide4_choose));
        String [] users = ((PreviewActivity)getActivity()).getEmps();
        for (String user : users) {
            arrayList.add(user);
        }

        if (mAdapter == null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, arrayList);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            mSpinner.setAdapter(adapter);
        }

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

    public String[] getAssignedTask() {
        String[] assignedTask = new String[]{mEditText.getText().toString().trim(), mSpinner.getSelectedItem().toString()};
        return assignedTask;
    }

    public void setUsers(String[] users, Context context) {
        ArrayList <String> arrayList = new ArrayList<>();
        arrayList.add(context.getResources().getString(R.string.preview_slide4_choose));
        for (String user : users) {
            if (!user.isEmpty()) {
                arrayList.add(user);
            }
        }

        mAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, arrayList);
        mAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        if (arrayList.size() > 1) {
            mPosition = 1;
        }
        mSpinner.setSelection(mPosition);
        /*if (mSpinner.getItemAtPosition(mPosition).toString().isEmpty()) {
            mPosition = 0;
            mSpinner.setSelection(mPosition);
        }*/
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