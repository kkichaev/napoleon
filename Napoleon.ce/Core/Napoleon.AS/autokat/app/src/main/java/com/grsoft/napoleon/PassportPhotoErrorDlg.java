package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.dataobjects.ScriptEx;
import com.grsoft.napoleon.views.RoundedDialog;

public class PassportPhotoErrorDlg extends RoundedDialog {
    public static final String PHOTO_COUNT = "photo_count";

    @Override
    protected int getLayoutId() {
        return R.layout.passport_photo_error_dlg;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(R.id.ok).setOnClickListener(w->dismiss());

        ((TextView)view.findViewById(R.id.message)).setText(getContext().getString(R.string.passport_photo_incomplete, getPhotoCount(getArguments().getInt(PHOTO_COUNT))));

        return view;
    }

    private String getPhotoCount(int pt) {
        return pt == ScriptEx.PASSPORT_RF ? getString(R.string.two) : getString(R.string.one_thing);
    }
}
