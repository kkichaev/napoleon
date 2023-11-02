package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Util;

import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	@Override
	protected void refreshData() {
		super.refreshData();
		
		TextView tv = (TextView)findViewById(R.id.tvSpikeQty);
		tv.setText(Util.IntToScaleStr(((PriceEx)price.getData()).spike, 0));
	}
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
}
