package com.grsoft.napoleon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.PresentEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PricePhotoName;
import com.grsoft.dataobjects.impl.PresentImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	
	@Override
	protected boolean getStartInPack() {
		return true;
	}
	
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	@Override
	protected void showItemImage() {
		PresentImpl prs = new PresentImpl();
		PresentEx pres = (PresentEx) prs.getData();
		pres.id = price.getData().id;
		prs.read();
		prs.close();
		
		LinearLayout photos = (LinearLayout)findViewById(R.id.llPhotoItems);
		if(photos != null) {
			View img = loadPhoto(pres.photoPath);
			if(img != null) {
				photos.addView(img);
			}
			
			for(PricePhotoName ppe : pres.photas) {
				img = loadPhoto(ppe.name);
				if(img != null) {
					photos.addView(img);
				}
			}
		}
	}

	View loadPhoto(final String fileName) {
		View ret = null;
		try{
			BitmapFactory.Options opt = new BitmapFactory.Options();
			Bitmap src = BitmapFactory.decodeFile(fileName, opt);
			
			LinearLayout layout = new LinearLayout(getApplicationContext());
			layout.setLayoutParams(new LayoutParams(250, 250));
			layout.setGravity(Gravity.CENTER);
			 
			ImageView iv = new ImageView(getApplicationContext());
			iv.setLayoutParams(new LayoutParams(220, 220));
			iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
			iv.setImageDrawable(new BitmapDrawable(src));
			iv.setOnClickListener(new OnClickListener() { 
				@Override public void onClick(View v) { 
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://" + fileName), "image/*");
					startActivity(intent);
//					PricePresentation.open(v.getContext(), fileName, price.getRowid());	
				} 
			});
			
			layout.addView(iv);
			ret = layout;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return ret;
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		PriceEx pe = (PriceEx)price.getData();
		int qty = qtyItems;
		if(cbPackets.isChecked())
			qty = (int)((long)qty * qtyInPack / Consts.QTY_SCALE);
		
		if( qty > 0 && qty < pe.minQty ) {
			Toast.makeText(this, " оличество меньше минимального заказа", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx pe = (PriceEx)price.getData();
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvCode);
		tv.setText(pe.id);
		
		
		tv = (TextView)findViewById(R.id.tvDiv);
		tv.setText(Util.IntToScaleStr(pe.minQty, Consts.QTY_SCALE));
		
		tv = (TextView)findViewById(R.id.tvExpired);
		tv.setText(pe.expired);
	}
}
