package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int cost = ((PriceEx)price.getData()).acost;
		
		if (cost > 0) {
			((TextView)findViewById(R.id.tvActionCost)).setText(Util.IntToScaleStr(cost, Consts.SUM_SCALE));
			((TextView)findViewById(R.id.tvPrice)).setText(Util.IntToScaleStr(priceVal, Consts.SUM_SCALE));
		}else
			findViewById(R.id.trActionCost).setVisibility(View.GONE);
	}
	
	@Override
	protected int getInputCost(Price p) {
		int cost = ((PriceEx)price.getData()).acost;
		return cost != 0 ? cost : priceVal;
	}
	
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
}
