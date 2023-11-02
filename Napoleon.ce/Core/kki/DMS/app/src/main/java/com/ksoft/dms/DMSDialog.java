package com.ksoft.dms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DMSDialog extends DialogFragment {
    EditText edAngle;
    EditText edMin;
    EditText edSec;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("градусы, минуты, секунды");
        View view = View.inflate(getContext(), R.layout.dmsdialog, null);
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder sb = new StringBuilder();
                EditText ed = ((Dialog)dialog).findViewById(R.id.edAngle);
                if (ed.length() > 0)
                    sb.append(ed.getText().toString().trim());
                else
                    sb.append("0");

                sb.append(getString(R.string.angle));

                ed = ((Dialog)dialog).findViewById(R.id.edMin);
                if (ed.length() > 0)
                    sb.append(ed.getText().toString().trim());
                else
                    sb.append("0");

                sb.append(getString(R.string.min));

                ed = ((Dialog)dialog).findViewById(R.id.edSec);

                if (ed.length() > 0)
                    sb.append(ed.getText().toString().trim());
                else
                    sb.append("0");

                sb.append(getString(R.string.sec));

                ((Calculator)getActivity()).addExpression(sb.toString());
            }
        });

        builder.setNegativeButton("Отменить", null);

        edAngle = view.findViewById(R.id.edAngle);
        edMin = view.findViewById(R.id.edMin);
        edSec = view.findViewById(R.id.edSec);


        edAngle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 3)
                    edMin.requestFocus();
            }
        });

        edMin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 2)
                    edSec.requestFocus();
            }
        });


        return builder.create();
    }
}
