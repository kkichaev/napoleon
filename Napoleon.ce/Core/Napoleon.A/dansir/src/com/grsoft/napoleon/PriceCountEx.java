package com.grsoft.napoleon;

import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx p = (PriceEx)price.getData();
		TextView tv = (TextView)findViewById(R.id.tvUnitName);
		tv.setText(p.packName);
		
		int upv = View.GONE;
		if( p.qtyInPack != Consts.QTY_SCALE ) {
			upv = View.VISIBLE;
//			tv = (TextView)findViewById(R.id.tvUnitPriceLabel);
//			String s = "цена за " + p.packName;
//			tv.setText(s);
			
			tv = (TextView)findViewById(R.id.tvUnitPrice);
			tv.setText(Util.IntToScaleStr((int)(((long)priceVal * p.qtyInPack) / Consts.QTY_SCALE), Consts.SUM_SCALE, Util.DEC_DELIM, false));
		} else {
		}
		findViewById(R.id.trUnitPrice).setVisibility(upv);
	}
}
