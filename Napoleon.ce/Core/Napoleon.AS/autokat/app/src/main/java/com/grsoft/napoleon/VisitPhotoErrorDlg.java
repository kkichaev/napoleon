package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.napoleon.views.RoundedDialog;

public class VisitPhotoErrorDlg extends RoundedDialog {
    public  static final  String TEXT = "text";

    @Override
    protected int getLayoutId() {
        return R.layout.visit_photo_error_dlg;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(R.id.ok).setOnClickListener(w->dismiss());

        if (getArguments() != null){
            String text = getArguments().getString(TEXT);

            if (text.length() > 0)
                ((TextView)view.findViewById(R.id.message)).setText(text);
        }
        return view;
    }
}
