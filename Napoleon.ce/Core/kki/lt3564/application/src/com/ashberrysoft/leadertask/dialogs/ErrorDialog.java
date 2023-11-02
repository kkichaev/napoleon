package com.ashberrysoft.leadertask.dialogs;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextThemeWrapper;

import com.ashberrysoft.leadertask.R;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ErrorDialog extends DialogFragment {

    private static final String CLASS_PATH = ErrorDialog.class.getName();
    private static final String DIALOG_TAG = CLASS_PATH + "DIALOG_TAG";
    private static final String EXTRA_MESSAGE = CLASS_PATH + "EXTRA_MESSAGE";
    private static final String DETAIL_MESSAGE = "Call this custom method: showDialog(FragmentManager manager)";
    public static final int REQUEST_CODE = R.id.blocking_process_dialog_request_code;

    private String mMessage;

    public static ErrorDialog newInstance(String message) {
        final Bundle b = new Bundle(1);
        b.putString(EXTRA_MESSAGE, message);

        final ErrorDialog d = new ErrorDialog();
        d.setArguments(b);

        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        mMessage = b != null ? b.getString(EXTRA_MESSAGE) : getArguments().getString(EXTRA_MESSAGE);
    }

    @Override
    public Dialog onCreateDialog(Bundle b) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setMessage(mMessage);
        ad.setTitle(R.string.d_error_title);
        ad.setPositiveButton(R.string.btn_ok, null);

        return ad.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(false);
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putString(EXTRA_MESSAGE, mMessage);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        throw new NoSuchMethodError(DETAIL_MESSAGE);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        throw new NoSuchMethodError(DETAIL_MESSAGE);
    }

    public void showDialog(FragmentManager manager) {
        if (manager.findFragmentByTag(DIALOG_TAG) == null) {
            super.show(manager, DIALOG_TAG);
        }
    }
}