package com.grsoft.napoleon;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.grsoft.view.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class QRView extends BaseActivity {
	static final String QR_TAG = "qr_code_tag";
	
	public final static int WHITE = 0xFFFFFFFF;
	public final static int BLACK = 0xFF000000;
	
	public final static int WIDTH = 400;
	public final static int HEIGHT = 400;
	
	public static void open(Context context, String qrcode) {
		Intent i = new Intent(context, QRView.class);
		i.putExtra(QR_TAG, qrcode);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrview);
		
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		String qr = b.getString(QR_TAG);
		
		ImageView iv = (ImageView)findViewById(R.id.ivQR);
		Bitmap bmp = encodeAsBitmap(qr);
		iv.setImageBitmap(bmp);
	}
	
	Bitmap encodeAsBitmap(String str) {
	    BitMatrix result;
	    try {
	        result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
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
