package com.grsoft.napoleon;

import android.widget.TextView;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx p = (PriceEx) price.getData();
		TextView tv;
		String text;
		tv = (TextView)findViewById(R.id.tvMinQty);
		int qip = p.qtyInPack;
		if( qip == 0 ) qip = Consts.QTY_SCALE;
		if( (p.minPart % qip) == 0 && p.minPart != 0 ) {
			text = Integer.toString(p.minPart / p.qtyInPack);
			text += " уп.";
		} else {
			text = Util.IntToScaleStr(p.minPart, Consts.QTY_SCALE);
		}
		tv.setText(text);
	}
}
