package com.ashberrysoft.leadertask.modern.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.fragments.LTVisibleBaseFragment;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.ui.fragments.BaseFragment;

import java.util.ArrayList;


public class SyncAddressDialog extends BaseDialog {

    public static final int CODE = R.id.dialog_sync_address;

    private static final String CLASS_PATH = SyncAddressDialog.class.getSimpleName();
    private static final String EXTRA_ADDRESS = CLASS_PATH + "EXTRA_ARRAY_ADDRESS";

    // ADAPTER
    private EditText etAddress;
    private String mAddress;


    public static SyncAddressDialog newInstance(Fragment target, String address) {
        final SyncAddressDialog d = new SyncAddressDialog();
        d.setTargetFragment(target, CODE);
        final Bundle b = new Bundle();
        b.putString(EXTRA_ADDRESS, address);
        d.setArguments(b);
        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        final Bundle bundle = getArguments();
        if ( bundle != null) {
            mAddress = (String) bundle.get(EXTRA_ADDRESS);
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.sync_address_dialog, null);
        etAddress = (EditText) v.findViewById((R.id.editAddress));

        etAddress.setText(mAddress);
        etAddress.requestFocus();

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getTargetFragment() instanceof LTVisibleBaseFragment) {
                    ((LTVisibleBaseFragment) getTargetFragment()).onFragmentResult(etAddress.getText().toString().trim(),CODE);
                    getDialog().dismiss();
                }
            }
        });
        ad.setNegativeButton(R.string.btn_cancel, null);
        return ad.show();
    }
}