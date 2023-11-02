package com.ashberrysoft.leadertask.modern.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.modern.activity.AccountActivity;

    public class LicenseDialog extends android.app.DialogFragment {

        private static String DIALOG_TAG = "LICENSE_DIALOG_TAG";
        // вызывать так LicenseDialog.newInstance().showDialog(getSupportFragmentManager());
        public static LicenseDialog newInstance() {
            final Bundle b = new Bundle(1);
            final LicenseDialog d = new LicenseDialog();
            d.setArguments(b);

            return d;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onCreate(Bundle b) {
            super.onCreate(b);
        }

        @SuppressLint("InflateParams")
        @Override
        public Dialog onCreateDialog(Bundle b) {
            final View v = LayoutInflater.from(getActivity()).inflate(R.layout.license_dialog, null);
            v.findViewById(R.id.acc_license_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(AccountActivity.newInstance(getActivity()));
                    getDialog().cancel();
                }
            });

            final LinearLayout lmain = new LinearLayout(getActivity());
            lmain.setOrientation(LinearLayout.VERTICAL);
            lmain.setGravity(Gravity.CENTER_HORIZONTAL);
            lmain.setBackgroundColor(Color.TRANSPARENT);
            lmain.addView(v);

            final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setCancelable(true);
            ad.setTitle(R.string.d_error_title);
            ad.setMessage(R.string.error_account_expired);
            ad.setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            ad.setView(lmain);
            ad.setCancelable(true);

            return ad.show();
        }

        @Override
        public void onSaveInstanceState(Bundle b) {
            super.onSaveInstanceState(b);
        }

        @Override
        public void onStart() {
            super.onStart();

            getDialog().setCancelable(true);
            getDialog().setCanceledOnTouchOutside(true);
        }


        public void showDialog(FragmentManager fragmentManager) {
            if (fragmentManager.findFragmentByTag(DIALOG_TAG) == null) {
                super.show(fragmentManager, DIALOG_TAG);
            }
        }
    }





