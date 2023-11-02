package com.grsoft.napoleon;

import com.grsoft.dataobjects.Agents;
import com.grsoft.dataobjects.PriceEx;

import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	@Override protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx pe = (PriceEx) price.getData();
		TextView tv = (TextView)findViewById(R.id.tvDescr);
		tv.setText(pe.descr);
		
		if(Agents.isDealer()) {
			priceVal = 0;
			onChangeCost(priceVal);
			
			((TextView) findViewById(R.id.tvQty)).setText("");
			findViewById(R.id.llInfo).setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void setCenterImage(String fileName) {
		super.setCenterImage(fileName);
		
		if(ivPresent2 != null) {
			Display display = getWindowManager().getDefaultDisplay(); 
			int height = display.getHeight();
			ivPresent2.getLayoutParams().height = height / 6;
			ivPresent2.setScaleType(ImageView.ScaleType.FIT_CENTER);
		}
	}
}
