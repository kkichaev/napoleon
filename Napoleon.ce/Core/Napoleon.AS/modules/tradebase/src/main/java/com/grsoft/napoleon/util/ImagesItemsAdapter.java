package com.grsoft.napoleon.util;

import java.util.List;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;


public class ImagesItemsAdapter extends BaseAdapter {
	protected List<VisitItem> data;
	protected Context context;
	int imageWidth = 0;
	int imageHeight = 0;
	int padding = 0;
	boolean scaleProp;
	
	public ImagesItemsAdapter(Context context, List<VisitItem> items){
		this.context = context;
		this.data = items;
		
		imageWidth = (int) context.getResources().getDimension(R.dimen.visit_preview_width);
		imageHeight = (int) context.getResources().getDimension(R.dimen.visit_preview_height);
		padding = (int) context.getResources().getDimension(R.dimen.visit_preview_padding);
		scaleProp = false;
	}

	public ImagesItemsAdapter(Context context, List<VisitItem> items, int w, int h, int padding, boolean scaleProp){
		this.context = context;
		this.data = items;
		
		imageWidth = w;
		imageHeight = h;
		this.padding = padding;
		this.scaleProp = scaleProp;
	}
	
	@Override public int getCount() { return data.size(); }
	@Override public Object getItem(int arg0) { return data.get(arg0); }

	@Override
	public long getItemId(int arg0) { return 0; }

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ImageView imageView;
        if (arg1 == null) {
            imageView = new ImageView(context);
            initImage(imageView, arg2);
        } else 
            imageView = (ImageView) arg1;

        String picture = ((VisitItem) getItem(arg0)).getImageFileName();
        imageView.setImageBitmap(createImage(picture));

        return imageView;
	}

	Bitmap rotate(Bitmap src, float rotation) {
		Matrix m = new Matrix();
		m.postRotate(rotation);

		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
	}
	protected Bitmap createImage(String picSrc) {
		Bitmap result = null;
        try{
        	BitmapFactory.Options opt = new BitmapFactory.Options();
        	opt.inSampleSize = 4;
        	result = BitmapFactory.decodeFile(picSrc, opt);
        	if(scaleProp) {
	        	float coefW = (float)imageWidth / result.getWidth();
	        	float coefH = (float)imageHeight / result.getHeight();
	        	float coef = Math.min(coefW, coefH);
	        	result = Bitmap.createScaledBitmap(result, (int)(result.getWidth() * coef), (int)(result.getHeight() * coef), true);
        	} else {
	        	result = Bitmap.createScaledBitmap(result, imageWidth, imageHeight, true);
        	}
			ExifInterface ei = new ExifInterface(picSrc);
        	int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        	switch(orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					result = rotate(result, 90);
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					result = rotate(result, 180);
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					result = rotate(result, 270);
					break;
			}
        }
        catch (Exception e){
        	e.printStackTrace();
        }
        
        return result;
	}		
	
	protected void initImage(ImageView image, ViewGroup arg2){
		if (image != null){
			if( arg2 instanceof Gallery )
				image.setLayoutParams(new Gallery.LayoutParams(imageWidth, imageHeight));
			else if( arg2 instanceof GridView )
				image.setLayoutParams(new GridView.LayoutParams(imageWidth, imageHeight));
			else			
				image.setLayoutParams(new ViewGroup.LayoutParams(imageWidth, imageHeight));
			
			image.setPadding(padding, 0, padding, 0);
			image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			image.setBackgroundColor(Color.RED);
		}
	}

	public void setData(List<VisitItem> data){
		this.data = data;
	}
}
