package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	@Override protected int getStartValue() { return 0; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View v = findViewById(R.id.orderRow);
		if( v != null )
			v.setVisibility((document instanceof OrderImpl) ? View.VISIBLE : View.GONE); 
	}
	
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	@Override
	protected void updateSumTextView() {
		super.updateSumTextView();
		
		if((document instanceof OrderImpl)) {
			OrderImpl ord = (OrderImpl)document;
			int sum = (int)ord.sum(); 
			OrderItem oi = (OrderItem) ord.findItem(price.getData().id);
			if( oi != null ) {
				sum -= (int)((long)oi.cost * oi.qty / Consts.QTY_SCALE);
			}
			sum += getSumValue();
			TextView tv = (TextView)findViewById(R.id.tvOrderSum);
			if( tv != null )
				tv.setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			
		}
	}
}
