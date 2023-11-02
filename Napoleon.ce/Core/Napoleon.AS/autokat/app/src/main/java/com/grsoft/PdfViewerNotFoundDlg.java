package com.grsoft;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.napoleon.R;
import com.grsoft.napoleon.views.RoundedDialog;

public class PdfViewerNotFoundDlg extends RoundedDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.pdf_viewer_not_found_dlg;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(R.id.ok).setOnClickListener(w->dismiss());
        return view;
    }
}
