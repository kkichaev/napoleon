package com.novotek.sales.main_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.novotek.dataobjects.ProjectData;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;

public class Feedback extends BaseView {


    View v;
    public static String TAG = Profile.class.toString();

    @Override
    protected int getResourceId() {
        return R.layout.feedback_view;
    }

    @Override
    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);

        v.findViewById(R.id.back).setOnClickListener(view -> getParentFragmentManager().popBackStack());

        for(String ph : ProjectData.commonInfo.phone) {
            TextView tv = (TextView)v.findViewById(R.id.phone);
            tv.setText(ph);
            tv.setOnClickListener(view -> ((MainActivity)getActivity()).makeCall(ph));
            break;
        }

        v.findViewById(R.id.send).setOnClickListener(this::sendMessage);

        model.requestInProgress.observe(this, inProgress -> {
            if(!inProgress) {
                TextInputLayout til = v.findViewById(R.id.text);
                til.getEditText().setText("");
            }
            v.findViewById(R.id.wait).setVisibility(inProgress ? View.VISIBLE : View.GONE);
        });
        return v;
    }

    void sendMessage(View view) {
        TextInputLayout til = v.findViewById(R.id.text);
        String text = til.getEditText().getText().toString();
        if(text.length() > 0) {
            model.sendFeedback(text, getContext());
        }
    }
}
