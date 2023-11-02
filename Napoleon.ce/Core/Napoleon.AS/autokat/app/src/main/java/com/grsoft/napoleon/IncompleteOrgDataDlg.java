package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.napoleon.views.RoundedDialog;

public class IncompleteOrgDataDlg extends RoundedDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.incomplete_org_data_dlg;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(R.id.ok).setOnClickListener(w->dismiss());
        return view;
    }
}
