package com.grsoft.napoleon;

import android.widget.TextView;

import com.grsoft.dataobjects.CostItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	@Override protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	protected void refreshData() {
		super.refreshData();
		
		PriceEx p = (PriceEx)price.getData();
		TextView tv;
		tv = (TextView)findViewById(R.id.tvReserv);
		tv.setText(Util.IntToScaleStr(p.reserv, Consts.QTY_SCALE));
		
		int costIndex = document.getSumType();
		if( costIndex >= p.cost.size())
			costIndex = 0;
		int oldCost = ((CostItemEx)p.cost.get(costIndex)).oldCost; 
		tv = (TextView)findViewById(R.id.tvOldCost);
		tv.setText(Util.IntToScaleStr(oldCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
	}
}
