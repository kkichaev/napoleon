package com.grsoft.ads;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class PasswordDlg extends DialogFragment {

    public static final String PASSWORD = "password";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.passwordmp);
        final View view = View.inflate(getActivity(), R.layout.passworddlg, null );
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText ed = view.findViewById(R.id.edPassw);
                boolean full = getArguments().getString(PASSWORD).equals(ed.getText().toString().trim());

                ((AdsNew)getActivity()).openConfigView(full);
            }
        });

        builder.setNegativeButton(R.string.cancel,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((AdsNew)getActivity()).openConfigView(false);
            }
        });

        return builder.create();
    }
}
