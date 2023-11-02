package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int priceUnit = priceVal;
		Price p = price.getData();
		if( p.weight > 0 ) {
			priceUnit = (int)((long)priceUnit * Consts.WEIGHT_SCALE / p.weight);			
		}
		TextView tv = (TextView)findViewById(R.id.tvPriceUnit);
		tv.setText(Util.IntToScaleStr(priceUnit, Consts.SUM_SCALE));
	}

}
