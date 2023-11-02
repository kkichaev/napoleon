package com.grsoft.napoleon;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Xml;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.grsoft.dataobjects.CheckResponse;
import com.grsoft.dataobjects.impl.CheckResponseImpl;
import com.grsoft.napoleon.util.Config;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PkoInfoEx extends PkoInfo {
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;

    @Override
    protected int getLayoutId() {
        return R.layout.pkoeditx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!pkoImpl.isEditable()) {
            edNumber.setEnabled(false);
            CheckResponseImpl ci = new CheckResponseImpl();
            CheckResponse cr = ci.getData();
            cr.created = pkoImpl.getData().created;
            if(ci.read()) {
                if(cr.link.length() == 0) {
                    findViewById(R.id.wait).setVisibility(View.VISIBLE);
                    getCheckLink(ci);
                } else {
                    showLink(cr.link);
                }
            }
        }
    }

    void showLink(String link) {
        ImageView iv = findViewById(R.id.ivQR);
        iv.setVisibility(View.VISIBLE);
        findViewById(R.id.wait).setVisibility(View.INVISIBLE);
        iv.setImageBitmap(encodeAsBitmap(link));
    }

    private void getCheckLink(CheckResponseImpl ci) {
        Thread t = new Thread(() -> {
            try {
                CheckResponse cr = ci.getData();

                String data = String.format("{\"fiscalSign\":\"%s\",\"fiscalDriveNumber\":\"%s\",\"fiscalDocumentNumber\":\"%s\"}"
                    ,cr.fsign, cr.fdrv, cr.fdoc);
                String url = "https://api.ofd-ya.ru/ofdapi/v2/getChequeLink";
                URL addr = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) addr.openConnection();
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Ofdapitoken", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpbm4iOlsiNjIyNzAxMDEzMSJdfQ.zva6TOzHVNvK9ec4w0JOyiBP8dQzMSfkDcp2VjQtlrc");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes(StandardCharsets.UTF_8));
                if(conn.getResponseCode() < 300) {
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream baso = new ByteArrayOutputStream();
                    byte[] chunk = new byte[4096];
                    int n;
                    while((n = is.read(chunk)) > 0) {
                        baso.write(chunk, 0, n);
                    }

                    String body = baso.toString(Xml.Encoding.UTF_8.name());
                    Gson gson = new Gson();
                    Map jdata = gson.fromJson(body, Map.class);
                    if(jdata.containsKey("link")) {
                        cr.link = jdata.get("link").toString();
                        ci.write();

                        runOnUiThread(() -> showLink(cr.link));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
    }


    Bitmap encodeAsBitmap(String str) {
        BitMatrix result;
        try {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int w = size.x * 2 / 3;
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, w, w, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int width = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[width * h];
        for (int y = 0; y < h; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, h);
        return bitmap;
    }
}
