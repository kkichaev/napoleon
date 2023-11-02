package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;

import android.view.View;
import android.widget.ImageView;

public class PriceCountEx extends PriceCount {
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx pe = (PriceEx)price.getData();
		ImageView iv = (ImageView)findViewById(R.id.ivHref);
		HrefHelper.setImageView(iv, pe.href, true);
	}
}
