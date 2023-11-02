package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.view.KeypadHelper;

import android.os.Bundle;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		cbPackets.setText("КГ");
	}
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }

	@Override protected KeypadHelper createKeypadHelper() { return new KeypadHelper(this, R.id.edCount, ((PriceEx)price.getData()).cantdiv > 0); }
	
	@Override
	protected boolean getStartInPack() {
		return (((PriceEx)price.getData()).cantdiv == 0);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		TextView tv = (TextView)findViewById(R.id.tvPckInfo);
		tv.setText((((PriceEx)price.getData()).cantdiv == 0) ? "Весовой товар" : "Штучный товар");
	}
}
