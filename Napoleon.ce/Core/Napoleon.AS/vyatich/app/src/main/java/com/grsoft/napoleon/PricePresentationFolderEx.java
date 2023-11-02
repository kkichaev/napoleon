package com.grsoft.napoleon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.grsoft.dataobjects.PriceMovie;
import com.grsoft.dataobjects.impl.PriceMovieImpl;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.SrcDataCounter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class PricePresentationFolderEx extends PricePresentationFolder {

    PriceMovieImpl pmi = new PriceMovieImpl();

    ProgressDialog progressDialog = null;

    @Override
    protected int getLayoutID() { return R.layout.price_present_folder_ex; }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.btnDownload).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { download();}
        });

        findViewById(R.id.btnPlay).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { play(); }
        });
        updateMovieButtons();
    }

    void download() {
        if(pmi.getData().url.length() == 0) {
            return;
        }

        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Загрузка файла...");
            progressDialog.show();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                loadFile();
            }
        });
        t.start();
    }

    void loadFile() {
        try {
            String url = pmi.getData().url;
            int index = url.lastIndexOf('.');
            URL addr = new URL(url);
            String extantion =  index > 0 ? url.substring(index) : "";

            HttpURLConnection conn = (HttpURLConnection)addr.openConnection();
            InputStream input = new BufferedInputStream(conn.getInputStream());

            File path = new File(Path.getDataDir());
            boolean ret = path.mkdir();
            File outFile = new File(path, UUID.randomUUID().toString().replace("-", "") + extantion);

            OutputStream output = new FileOutputStream(outFile);
            int read = 0;

            byte[] bytes = new byte[1024];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            output.close();
            input.close();

            pmi.getData().file = outFile.getAbsolutePath();
            pmi.write();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    updateMovieButtons();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    Toast.makeText(PricePresentationFolderEx.this, "Ошибка при скачивании файла", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    void hideProgress() {
        if(progressDialog != null) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressDialog = null;
        }
    }

    void play() {
        File file = new File(pmi.getData().file);

        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this, getString(R.string.fileprovider_authorities), file);
        }else
            uri = Uri.fromFile(file);

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "video/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        pmi.close();
        hideProgress();
    }

    @Override
    protected void setText(View view, PresentationData pd) {
        super.setText(view, pd);
        PriceMovie pm = pmi.getData();
        pm.id = pd.id;
        if(!pmi.read()) {
            pm.url = "";
            pm.file = "";
        }
        updateMovieButtons();
    }

    private void updateMovieButtons() {
        PriceMovie pm = pmi.getData();
        findViewById(R.id.btnDownload).setEnabled(pm.url.length() > 0);
        findViewById(R.id.btnPlay).setEnabled(pm.file.length() > 0);
    }

    @Override
    protected Fragment createFragment() {
        return new PriceFragEx(this);
    }

    public static class PriceFragEx extends PriceFrag {

        public PriceFragEx(PricePresentationFolderEx owner) {
            super(owner);
        }
    }
}
