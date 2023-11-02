package com.grsoft.napoleon.script_wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.napoleon.R;

public class PassportInvalidDlg2 extends PassportInvalidDlg {
    public static final String RESULT_KEY = "PassportInvalidDlg2";
    public static final int OPERATOR = 3;
    @Override
    protected int getLayoutId() {
        return R.layout.passport_invalid_dlg2;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(R.id.operator).setOnClickListener(this::operator);

        return view;
    }

    private void operator(View view) {
        setSelected(OPERATOR);
    }

    @Override
    public String getResultKey() {
        return RESULT_KEY;
    }
}
