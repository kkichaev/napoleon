package com.grsoft.napoleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import com.grsoft.util.Updater;

import java.lang.ref.WeakReference;

public class UpdateMessageBox {
    public static final String UPDATE_KIND = "update";

    public static void show(String message, String title, Activity context) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle(title);
        b.setMessage(message);

        b.setNegativeButton(context.getString(R.string.close), (dialog, which) -> dialog.dismiss());
        b.setPositiveButton(context.getString(R.string.install), (dialog, which) -> {
            new Upd(context).execute(context);
        });

        context.runOnUiThread(() -> b.create().show());
    }

    static class Upd extends Updater {
        WeakReference<Context> context;
        Upd(Context c) { context = new WeakReference<>(c); }

        protected void onPreExecute() {
            Context c = context.get();
            if(c != null) {
                Toast.makeText(context.get(), R.string.check_updating, Toast.LENGTH_SHORT).show();
            }
        };

        protected void onPostExecute(Boolean result) {
            Context c = context.get();
            if(!result && c != null) {
                Toast.makeText(context.get(), R.string.update_not_found, Toast.LENGTH_SHORT).show();
            }
        };
    }

}
