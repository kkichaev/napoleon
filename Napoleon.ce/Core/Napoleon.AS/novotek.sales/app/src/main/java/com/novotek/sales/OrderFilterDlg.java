package com.novotek.sales;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class OrderFilterDlg extends AppCompatDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.order_filter_dlg, null);
        if(getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        v.findViewById(R.id.trState).setOnClickListener( view -> {
            new FilterStateDlg().show(getParentFragmentManager(), "");
            dismiss();
        });
        return v;
    }

    public static class FilterStateDlg extends AppCompatDialogFragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.order_filter_state, null);
            if(getDialog() != null && getDialog().getWindow() != null) {
                getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            v.findViewById(R.id.back).setOnClickListener(view -> {
                new OrderFilterDlg().show(getParentFragmentManager(), "");
                dismiss();
            });
            return v;
        }
    }
}
