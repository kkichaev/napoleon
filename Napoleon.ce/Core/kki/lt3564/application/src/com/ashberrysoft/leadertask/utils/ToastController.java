package com.ashberrysoft.leadertask.utils;

import android.content.Context;
import android.widget.Toast;

public final class ToastController {

    // SINGLETON
    private static ToastController sInstance;

    // BASE
    private final Context mContext;

    // VALUE
    private Toast mToast;

    public static ToastController getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ToastController.class) {
                if (sInstance == null) {
                    sInstance = new ToastController(context);
                }
            }
        }
        return sInstance;
    }

    private ToastController(Context context) {
        mContext = context.getApplicationContext();
    }

    public void showToast(CharSequence message) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
        mToast.show();
    }

    public void showToast(int messageRes) {
        showToast(mContext.getString(messageRes));
    }
}