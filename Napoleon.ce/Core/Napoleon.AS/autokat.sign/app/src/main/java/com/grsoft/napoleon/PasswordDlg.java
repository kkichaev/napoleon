package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.views.RoundedDialog;

public class PasswordDlg extends RoundedDialog {
    public static final String RESULT = "PwdResult";

    public static String getPassword() {
        ConfigImpl ci = new ConfigImpl();
        ci.getData().key = "SignPWD";
        String cfgPwd = ( ci.read() ) ? ci.getData().value : "";
        ci.close();
        return cfgPwd;
    }

    @Override protected int getLayoutId() {return R.layout.password_dialog;}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final String cfgPwd = getPassword();

        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.findViewById(R.id.ok).setOnClickListener(v1 -> {
            EditText t = ((TextInputLayout)v.findViewById(R.id.password)).getEditText();
            String pwd = t.getText().toString();

            if(pwd.equals(cfgPwd)) {
                Bundle res = new Bundle();
                res.putBoolean(RESULT, true);
                getParentFragmentManager().setFragmentResult(RESULT, res);
                dismiss();
            }
        });

        v.findViewById(R.id.clear).setOnClickListener(v1 -> {
            EditText t = ((TextInputLayout)v.findViewById(R.id.password)).getEditText();
            t.setText("");
        });
        return v;
    }
}
