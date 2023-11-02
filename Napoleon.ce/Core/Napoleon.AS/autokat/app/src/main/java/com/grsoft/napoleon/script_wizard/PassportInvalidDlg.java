package com.grsoft.napoleon.script_wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.napoleon.R;
import com.grsoft.napoleon.views.RoundedDialog;

public class PassportInvalidDlg extends RoundedDialog {
    public static final String RESULT_KEY = "PassportInvalidDlg";
    public static final int REPEAT_SELECT = 0;
    public static final int PCA_SELECT = 1;
    @Override
    protected int getLayoutId() {
        return R.layout.passport_invalid_dlg;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(R.id.repeat).setOnClickListener(this::repeat);
        view.findViewById(R.id.pca).setOnClickListener(this::pca);

        setCancelable(false);
        return view;
    }

    public void pca(View view){
        setSelected(PCA_SELECT);
    }

    public void repeat(View view){
        setSelected(REPEAT_SELECT);
    }

    public void setSelected(int repeatSelect) {
        Bundle res = new Bundle();
        res.putInt(getResultKey(), repeatSelect);
        getParentFragmentManager().setFragmentResult(getResultKey(), res);
        dismiss();
    }

    public String getResultKey(){
        return RESULT_KEY;
    }
}
