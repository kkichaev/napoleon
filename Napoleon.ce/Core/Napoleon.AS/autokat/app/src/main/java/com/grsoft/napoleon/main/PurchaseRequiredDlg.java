package com.grsoft.napoleon.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.grsoft.napoleon.R;
import com.grsoft.napoleon.views.RoundedDialog;

public class PurchaseRequiredDlg extends RoundedDialog {
    @Override
    protected int getLayoutId() { return R.layout.input_dialog; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.findViewById(R.id.ok).setOnClickListener(w->dismiss());
        return v;
    }
}
