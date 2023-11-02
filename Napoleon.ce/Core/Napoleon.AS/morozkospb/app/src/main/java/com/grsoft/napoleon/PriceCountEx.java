package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;

import android.os.Bundle;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (DocType.getCurDoc() == OrderDoc.instance()) {
			String text = CostStrategyEx.getOrgText(document, price.getData().id);
			TextView tv = (TextView) findViewById(R.id.tvText);
			tv.setText(text);
		}
	}
}
