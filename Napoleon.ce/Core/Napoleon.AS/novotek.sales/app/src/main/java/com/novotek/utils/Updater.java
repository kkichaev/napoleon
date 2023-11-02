package com.novotek.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.novotek.sales.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Updater {
    public interface Handler {
        void requestDone();
        void progress(Progress progress);
    }

    public static class Progress {
        public int total;
        public int current;

        public Progress(int t, int c) {
            total = t;
            current = c;
        }
    }

    static final String APK_URL = "http://mt.serviko.ru/distr/ServikoDelivery.apk";

    static public void update(Context context, Handler handler) {

        Thread t = new Thread(() -> {
            try {
                URL addr = new URL(APK_URL);
                File outFile = new File(context.getExternalFilesDir(""), "app.apk");

                HttpURLConnection conn = (HttpURLConnection)addr.openConnection();
                int totalBytes = conn.getContentLength();
                InputStream input = (conn).getInputStream();
                OutputStream output = new FileOutputStream(outFile);

                if(handler != null) {
                    handler.progress(new Progress(totalBytes, 0));
                }

                int read = 0, curRead = 0;

                byte[] bytes = new byte[20 *1024];
                while ((read = input.read(bytes)) != -1) {
                    output.write(bytes, 0, read);
                    if(handler != null) {
                        curRead += read;
                        handler.progress(new Progress(totalBytes, curRead));
                    }
                }
                output.close();
                input.close();

                Intent intent = new Intent(Intent.ACTION_VIEW);

                Uri uri = null;
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        uri = FileProvider.getUriForFile(context,context.getString(R.string.file_provider), outFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    uri = Uri.fromFile(outFile);
                }
                if(handler != null) {
                    handler.requestDone();
                }
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();

                if(handler != null) {
                    handler.requestDone();
                }
            }

        });
        t.start();
    }
}
