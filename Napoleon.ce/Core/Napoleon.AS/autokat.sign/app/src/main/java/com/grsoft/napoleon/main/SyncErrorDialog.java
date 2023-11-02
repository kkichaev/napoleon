package com.grsoft.napoleon.main;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.grsoft.napoleon.R;
import com.grsoft.napoleon.views.RoundedDialog;

public class SyncErrorDialog extends RoundedDialog {

    static final String MSG = "msg";

    public SyncErrorDialog(String message) {
        Bundle b = new Bundle();
        b.putString(MSG, message);
        setArguments(b);
    }

    @Override
    protected int getLayoutId() { return R.layout.error_dialog; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        String message = getArguments().getString(MSG, "");
        ((TextView)v.findViewById(R.id.error_text)).setText(Html.fromHtml(message));

        v.findViewById(R.id.ok).setOnClickListener(view -> dismiss());
        return v;
    }
}
