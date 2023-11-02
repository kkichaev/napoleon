package com.ashberrysoft.leadertask.dialogs;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;

import com.ashberrysoft.leadertask.R;

public class SetColorDialogBuilder {

    public static final int NO_COLOR = -1;

    private final AmbilWarnaDialog mDialog;

    public SetColorDialogBuilder(Context context, int color, OnAmbilWarnaListener listener) {
        final AmbilWarnaCallback callback = new AmbilWarnaCallback(listener);

        mDialog = new AmbilWarnaDialog(context, color, callback);
        mDialog.getAlertDialogBuilder().setNeutralButton(R.string.by_default, callback);
    }

    public void show() {
        mDialog.show();
    }

    private static final class AmbilWarnaCallback implements OnAmbilWarnaListener, DialogInterface.OnClickListener {

        private final OnAmbilWarnaListener mListener;

        public AmbilWarnaCallback(OnAmbilWarnaListener listener) {
            mListener = listener;
        }

        @Override
        public void onOk(AmbilWarnaDialog dialog, int color) {
            mListener.onOk(dialog, color);
        }

        @Override
        public void onCancel(AmbilWarnaDialog dialog) {
            mListener.onCancel(dialog);
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            mListener.onOk(null, NO_COLOR);
        }
    }
}