package com.serviko.sales.login_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.serviko.dataobjects.ws.ReqCodeResult;
import com.serviko.sales.BuildConfig;
import com.serviko.sales.R;

public class CheckCode extends Fragment {
    public static final String RESULT_CODE = "check_code";

    Model model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.check_code_fragment_new, container, false);

        model = new ViewModelProvider(getActivity()).get(Model.class);
        updateText(v, model.getSmsMode().getValue());

        View doButton = v.findViewById(R.id.doButton);
//        model.getRequestInProgress().observe(this, inProgress -> {
//            doButton.setEnabled(!inProgress);
//        });
        doButton.setOnClickListener(view -> {
            ReqCodeResult r = model.getRequestResult().getValue();
            if(r != null) {
                try {
                    String inpCode = ((TextInputLayout) v.findViewById(R.id.code)).getEditText().getText().toString();
                    if(BuildConfig.DEBUG) {
                        inpCode = Integer.toString(r.code);
                    }

                    int code = Integer.parseInt(inpCode);
                    if (code == r.code) {
                        getParentFragmentManager().setFragmentResult(RESULT_CODE, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
    }

    void updateText(View v, boolean smsMode) {
        TextView tv = v.findViewById(R.id.phone_hint);
        tv.setText(smsMode ? R.string.type_from_sms : R.string.type_from_phone);

        tv = v.findViewById(R.id.doButtonSecond);
        tv.setText(smsMode ? R.string.get_code_by_phone_u : R.string.get_code_by_sms_u);
    }
}
