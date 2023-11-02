package com.grsoft.napoleon;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.DeliveryImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import java.text.SimpleDateFormat;

public class DeliveryPayment extends Activity {

    public static void open(Context context, DeliveryImplBase doc) {
        Intent i = new Intent(context, DeliveryPayment.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_qy);

        DeliveryImpl di = new DeliveryImpl();
        long rc = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
        di.read(rc);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Delivery d = di.getData();
        String sum = Util.IntToScaleStr(d.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false);
        String text = String.format("Оплата накладной %s от %s %s"
                ,d.number
                ,sdf.format(d.date)
                ,sum
        );

        ((TextView)findViewById(R.id.delivery)).setText(text);

        ConfigImpl ci = new ConfigImpl();
        StringBuilder sb = new StringBuilder();
        if(ci.getValue(sb, "KASPI_Prefix")) {
            String qr = ci.getData().value + d.number + "&amount=" + sum;
            Bitmap qri = encodeAsBitmap(qr);
            ((ImageView)findViewById(R.id.qrcode)).setImageBitmap(qri);
        }
    }

    Bitmap encodeAsBitmap(String str) {
        BitMatrix result;
        try {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int w = size.x * 1 / 2;
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
