package com.grsoft.napoleon;

import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void updateSumTextView() {
		super.updateSumTextView();
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvWeight);
		tv.setText(Util.IntToScaleStr((long)qtyItems * price.getData().weight / Consts.QTY_SCALE, Consts.WEIGHT_SCALE, Util.DEC_DELIM, false));
	}
}
