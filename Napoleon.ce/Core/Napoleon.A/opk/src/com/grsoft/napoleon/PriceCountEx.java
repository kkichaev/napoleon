package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cbPackets.setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx pe = (PriceEx) price.getData();
		TextView tv;
		tv = (TextView)findViewById(R.id.tvPackText);
		
		tv.setText(pe.packText);
	}
}
