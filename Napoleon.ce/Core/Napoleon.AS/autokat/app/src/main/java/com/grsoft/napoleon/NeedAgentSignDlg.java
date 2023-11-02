package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.napoleon.views.RoundedDialog;

public class NeedAgentSignDlg extends RoundedDialog {
    public static String RESULT_KEY = "";
    @Override
    protected int getLayoutId() {
        return R.layout.need_agent_sign_dlg;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(R.id.ok).setOnClickListener(w-> doOk());
        return view;
    }

    private void doOk() {
        dismiss();
        getParentFragmentManager().setFragmentResult(RESULT_KEY, null);
    }
}
