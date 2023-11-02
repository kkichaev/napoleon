package com.grsoft.ads;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.grsoft.ads.dataobjects.OrderEx;
import com.grsoft.ads.dataobjects.OrderPhotoItem;
import com.grsoft.ads.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.CameraPreview;
import com.grsoft.ads.R;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;

public class OrderPhoto extends BaseActivity {
	public static final String TAB_NAME = "order_photos";
	public static final String TAB_CAPTION = "Фото";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_photo);
		final long rowid = getIntent()
				.getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		
		OrderImplEx oi = new OrderImplEx();
		ImageButton btnPhoto = (ImageButton) findViewById(R.id.btnPhoto); 
		btnPhoto.setEnabled(oi.read(rowid) && oi.isEditable());
		oi.close();
		
		btnPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OrderImplEx orderImpl = new OrderImplEx();
				
				if (orderImpl.read(rowid))
					CameraPreview.open(v.getContext(), orderImpl, OrderDoc.instance());
				
				orderImpl.close();
			}
		});
		
		Gallery gallery = (Gallery)findViewById(R.id.gallery);
		ImagesAdapter adapter = new ImagesAdapter(rowid);
		gallery.setAdapter(adapter);
		gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> gallery, View arg1, int pos,
					long arg3) {
				
				showImage((BaseAdapter)gallery.getAdapter(), pos);
			}
		});
	}
	
	private void showImage(BaseAdapter adapter, int pos){
		OrderPhotoItem opi = (OrderPhotoItem) adapter.getItem(pos);
		
		if(opi != null){
			ImageView preview = (ImageView) findViewById(R.id.imageView);
			Bitmap bm = BitmapFactory.decodeFile(new String(opi.id));
	    	preview.setImageBitmap(bm);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		BaseAdapter adapter = ((BaseAdapter)((Gallery)findViewById(R.id.gallery))
				.getAdapter());
		
		if(adapter != null){
			adapter.notifyDataSetChanged();
		
			if (adapter.getCount() > 0)
				showImage(adapter, 0);
		}
	}
	
	class ImagesAdapter extends BaseAdapter
	{
		private OrderImplEx orderImpl;
		public ImagesAdapter(long rowid){
			this.orderImpl = new OrderImplEx();
			orderImpl.read(rowid);
			orderImpl.close();
		}
		
		@Override
		public int getCount() { return ((OrderEx)orderImpl.getData()).photos.size(); }

		@Override
		public Object getItem(int arg0) { return ((OrderEx)orderImpl.getData()).photos.get(arg0); }

		@Override
		public long getItemId(int arg0) { return 0; }

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2)
		{
			byte[] picture = ((OrderPhotoItem) getItem(arg0)).id;
			
			ImageView imageView;
	        if (arg1 == null) 
	        {  
	            imageView = new ImageView(OrderPhoto.this);
	            imageView.setLayoutParams(new Gallery.LayoutParams(100, 85));
	            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
	            imageView.setBackgroundColor(Color.RED);
	        } else 
	            imageView = (ImageView) arg1;

	        String picSrc = new String(picture);
	        try{
	        	BitmapFactory.Options opt = new BitmapFactory.Options();
	        	opt.inSampleSize = 4;
	        	Bitmap bm = BitmapFactory.decodeFile(picSrc, opt);
	        	imageView.setImageBitmap(bm);
	        }
	        catch (Exception e){
	        	e.printStackTrace();
	        }

	        return imageView;
		}		
		
		@Override
		public void notifyDataSetChanged() {
			orderImpl.read(orderImpl.getRowid(), false);
			orderImpl.close();
			super.notifyDataSetChanged();
		}
	}
}
