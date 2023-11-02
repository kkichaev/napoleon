package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.DialogFragment;
import android.widget.ListView;

public class DeliveryExceedDlg extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle(R.string.warning);
        View view = inflater.inflate(R.layout.delivery_exceed_dlg, null, false);

        ListView list = view.findViewById(R.id.list);
        list.setAdapter(((DocumentsEx)getActivity()).getDlvAdapter());

        view.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ((DocumentsEx)getActivity()).deliveryDialogOKClick();
            }
        });
        return view;
    }
}
