package com.grsoft.napoleon;

import android.widget.TextView;

import com.grsoft.dataobjects.OffTakeCoeff;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OffTakeCoeffImpl;
import com.grsoft.napoleon.documents.OffTakeHistory;

public class PriceCountEx extends PriceCount {
	@Override
	protected void makeSaleHistory(Price p) {
		OffTakeHistory.inflator = new OffTakeCoeffReader(p);
		super.makeSaleHistory(p);
	}
	
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		TextView tv = (TextView)findViewById(R.id.tvUnit);
		tv.setText(((PriceEx)price.getData()).unit);
	}
}

class OffTakeCoeffReader extends OffTakeHistory.OffTakeInflator {
	int coef = OFF_TAKE_COEF;
	public OffTakeCoeffReader(Price p) {
		OffTakeCoeffImpl ci = new OffTakeCoeffImpl();
		OffTakeCoeff ce = ci.getData();
		ce.id = p.id;
		if( ci.read() )
			coef = ce.coef;
		ci.close();
	}
	
	@Override public int getOffTake() { return coef; }
}