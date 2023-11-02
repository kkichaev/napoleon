package com.grsoft.util;
import com.grsoft.aceteam.R;

import java.io.File;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;

public class BitmapUtils {
	public static BitmapDrawable createBitmap(String path, int size) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inSampleSize = 3;

		File f = new File(path);
		if( f.canRead() == false )
			return null;
		
		Bitmap src = BitmapFactory.decodeFile(path, opt);
		double coef = (double) size / Math.max(src.getWidth(), src.getHeight());

		BitmapDrawable b;
		if (coef == 1.0)
			b = new BitmapDrawable(src);
		else
			b = new BitmapDrawable(Bitmap.createScaledBitmap(src,
					(int) (src.getWidth() * coef + 0.5), (int) (src.getHeight()
							* coef + 0.5), true));

		b.setBounds(0, 0, size, size);

		return b;
	}
	
	public static Drawable createBitmap(Context ctx, String path, int w, int h) {
	    BitmapFactory.Options options = initOptions(path, w, h);
	    Bitmap bitmap = BitmapFactory.decodeFile(path, options);
	    
	    try {
	    	ExifInterface exif = new ExifInterface(path);
	    	int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
	    	int rotationInDegrees = exifToDegrees(rotation);
	    	
	    	Matrix matrix = new Matrix();
	    	if (rotation != 0f) {
	    		matrix.preRotate(rotationInDegrees);
	    		bitmap =  Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	    	}
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
	    
	    BitmapDrawable result = new BitmapDrawable(ctx.getResources(), bitmap);
	    return  result;
	}
	
	private static int exifToDegrees(int exifOrientation) {        
	    if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; } 
	    else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; } 
	    else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }            
	    return 0;    
	 }

	public static Drawable createBitmap(Context ctx, byte[] img, int w, int h) {
	    BitmapFactory.Options options = initOptions(img, w, h);
	    BitmapDrawable result = new BitmapDrawable(ctx.getResources(), BitmapFactory.decodeByteArray(img, 0, img.length, options));
	    return  result;
	}

	protected static BitmapFactory.Options initOptions(byte[] img, int w, int h) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeByteArray(img, 0, img.length, options);
	    options.inSampleSize = calcss(options, w, h);
	    options.inJustDecodeBounds = false;
		return options;
	}
	
	protected static BitmapFactory.Options initOptions(String path, int w, int h) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);
	    options.inSampleSize = calcss(options, w, h);
	    options.inJustDecodeBounds = false;
		return options;
	}
	
	public static Bitmap createBitmap(String path, int w, int h){
		BitmapFactory.Options options = initOptions(path, w, h);
	    return  BitmapFactory.decodeFile(path, options);
	}
	
	public static int calcss(BitmapFactory.Options options, int rw, int rh) {
	    final int h = options.outHeight;
	    final int w = options.outWidth;
	    int s = 1;
	
	    if (h > rh || w > rw) {
	
	        final int hh = h / 2;
	        final int hw = w / 2;
	
	        while ((hh / s) > rh && (hw / s) > rw) 
	            s *= 2;
	    }

	    return s;
	}

	public static Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		Bitmap src = BitmapFactory.decodeFile(photoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);
		}

		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true; //Deprecated API 21

		return BitmapFactory.decodeFile(photoPath, bmOptions);
	}
}
