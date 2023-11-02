package com.photoprint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class PhotoPrintActivity extends Activity {
    @SuppressWarnings("unused")
	private final static String TAG = "PhotoPrintActivity";
    /*Директория для выгрузки временных файлов*/
	final static String DIR_OUTPUT = "/mnt/sdcard/test/"; 
	
	/** Called when the activity is first created. */
	/**
	 * Качать отсюда HP IPrint Photo
	 * http://www8.hp.com/us/en/products/smart-phones-handhelds-calculators/mobile-apps/app_details.html?app=tcm:245-799203&platform=tcm:245-799126&jumpid=ex_r11400_go/iprintphotoforandroid
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				/*Создаем рисунок */
				Bitmap mBitmap = Bitmap.createBitmap(2400, 3320, Bitmap.Config.RGB_565);
				Canvas mCanvas = new Canvas(mBitmap);
				mCanvas.drawARGB(100, 100, 0, 100);
				Paint paint = new Paint();
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(Color.WHITE);
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(1);
				paint.setTextSize(30);
				mCanvas.drawText("hello world!", 10, 30, paint);
				mCanvas.drawCircle(1200, 1660, 800, paint);
				
				/*имя файла*/
				String filename = String.valueOf(System.currentTimeMillis());
				File outFile = new File(DIR_OUTPUT + filename);

				/*сохраняем рисунок в файл*/
				try{
					OutputStream outStream = new FileOutputStream(outFile);
					mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
					outStream.flush();
					outStream.close();
				}catch(Exception e){
					e.printStackTrace();
				}

				/*запускаем HP Photo*/
				Intent intent = new Intent("org.androidprinting.intent.action.PRINT")
				.addCategory(Intent.CATEGORY_DEFAULT)
				.setDataAndType(Uri.fromFile(outFile),"image/*");
				startActivityForResult(intent, 0);
			}
		});
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	/*После закрытия окна печати, чистим временную директорию*/
    	File tempDir = new File(DIR_OUTPUT);
    	
    	for(File f : tempDir.listFiles())
    		f.delete();
    }
}