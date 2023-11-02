package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.TextView;

import com.grsoft.dataobjects.PriceEx;

public class PriceCountEx extends PriceCount {
	
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView tvUnit = (TextView) findViewById(R.id.tvUnits);
		tvUnit.setText(((PriceEx)price.getData()).whUnit);
	}
}
