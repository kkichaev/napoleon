package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class BarcodeView extends Activity {
    private static final String BARCODE = "barcode";

    public static void open(Context context, String bc) {
        Intent i = new Intent(context, BarcodeView.class);
        i.putExtra(BARCODE, bc);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_view);
        ImageView barcode = findViewById(R.id.barcode);
        barcode.setImageBitmap(encodeAsBitmap(getIntent().getStringExtra(BARCODE)));
    }


    Bitmap encodeAsBitmap(String str) {
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;

        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.EAN_13, getResources().getDisplayMetrics().widthPixels,
                    (int) getResources().getDimension(R.dimen.bc_height), null);
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
