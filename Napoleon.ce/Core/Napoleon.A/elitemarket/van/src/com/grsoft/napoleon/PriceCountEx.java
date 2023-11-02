package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.impl.WSOrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if( document instanceof WSOrderImpl )
			findViewById(R.id.trVanQty).setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		PricePrint p = (PricePrint)price.getData();
		TextView tv = (TextView)findViewById(R.id.tvVanQty);
		tv.setText(Util.IntToScaleStr(p.vanQty, Consts.QTY_SCALE));
	}
	
	
	@Override
	protected boolean isComplexSalesHistory() {
		DocType cd = DocType.getCurDoc();
		return (cd != OrderDoc.instance()) ? false : super.isComplexSalesHistory();
	}
}
