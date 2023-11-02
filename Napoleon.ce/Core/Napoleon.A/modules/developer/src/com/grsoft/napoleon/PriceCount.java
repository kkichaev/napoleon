package com.grsoft.napoleon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;


public class PriceCount extends PriceCountW {
	ImageView ivPresent2;
	protected int img2Hide = View.INVISIBLE;
	
	@Override
	protected void postOnCreate() {
		ivPresent2 = (ImageView) findViewById(R.id.ivPresent2);
		if( ivPresent2 != null )
			ivPresent2.setVisibility(img2Hide);
	}
	
	@Override protected int getContentViewId() { return R.layout.pricecount_new; }
	
	@Override
	protected boolean isShowImage() {
		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		return cfg.showImageInPriceCount;
	}
	
	@Override
	protected void setItemImage(final String fileName) {
		if( ivPresent != null )
			ivPresent.setVisibility(View.GONE);
		if( ivPresent2 != null )
			ivPresent2.setVisibility(img2Hide);
		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		switch(cfg.imagePosInPriceCount){
		case 0:
			if( ivPresent != null )
				ivPresent.setVisibility(View.VISIBLE);
			super.setItemImage(fileName);
			break;
		case 1:
			if( ivPresent2 != null )
				ivPresent2.setVisibility(View.VISIBLE);
			setCenterImage(fileName);
			break;
		}
	}

	protected void setCenterImage(final String fileName) {
		try{
			BitmapFactory.Options opt = new BitmapFactory.Options();
			Bitmap src = BitmapFactory.decodeFile(fileName, opt);
			ivPresent2.setImageDrawable(new BitmapDrawable(src));
			ivPresent2.setVisibility(View.VISIBLE);
			ivPresent2.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { PricePresentation.open(v.getContext(), fileName, price.getRowid());	} });
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
