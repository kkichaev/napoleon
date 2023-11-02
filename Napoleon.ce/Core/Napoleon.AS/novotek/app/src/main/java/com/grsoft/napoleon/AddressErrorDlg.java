package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.napoleon.views.RoundedDialog;

public class AddressErrorDlg extends RoundedDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.address_error_dlg;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(R.id.ok).setOnClickListener(w->dismiss());
        ((TextView)view.findViewById(R.id.message)).setText(Html.fromHtml(getString(R.string.address_not_found)));
        return view;
    }
}
