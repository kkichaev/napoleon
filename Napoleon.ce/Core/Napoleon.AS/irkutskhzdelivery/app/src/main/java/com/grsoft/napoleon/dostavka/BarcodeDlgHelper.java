package com.grsoft.napoleon.dostavka;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.grsoft.camera.BarcodeHandler;
import com.grsoft.camera.CameraActivity;

public class BarcodeDlgHelper {
    interface OpenItem{
        boolean tryOpen(String barcode);
    }
    public static final String BARCODE_KEY = "barcode";

    public static void prepareNotBarcodeDlg(Dialog dialog, Bundle bundle) {
        String barcode = bundle.getString(BarcodeDlgHelper.BARCODE_KEY);
        ((AlertDialog)dialog).setMessage(String.format("Ўтрих код '%s' не найден", barcode));
    }

    public static Dialog createNotBarcodeDlg(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.alert);
        builder.setMessage("");
        builder.setPositiveButton(R.string.ok, null);

        return builder.create();
    }

    public static boolean openScanner(Activity activity, OpenItem openItem) {
        CameraActivity.openBCScanner(activity, new BarcodeHandler() {

            @Override
            public boolean onReadBarcode(String barcode, int type, long elapsesMs) {
                boolean res =  openItem.tryOpen(barcode);

                if (!res) {
                    Bundle bundle = new Bundle();
                    bundle.putString(BARCODE_KEY, barcode);
                    activity.runOnUiThread(() -> activity.showDialog(R.id.not_barcode_dlg, bundle));
                }

                return true;
            }
        });

        return true;
    }
}
