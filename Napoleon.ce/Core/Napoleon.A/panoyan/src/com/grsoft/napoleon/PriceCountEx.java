package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;

import android.os.Bundle;
import android.text.style.BackgroundColorSpan;
import android.widget.TextView;

public class PriceCountEx extends PriceCount {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		((CheckBox)findViewById(R.id.cbPackets)).setText("штуками");
	}
	
//	@Override
//	protected void refreshData() {
//		super.refreshData();
//		
//		Itemsable doc = (Itemsable) OrderDoc.instance().create();
//		int qty = doc.getItemValue(price.getData());
//		String text = PriceHelper.getQtyText(qty, price.getData().qtyInPack);
//		if(text != null)
//			((TextView)findViewById(R.id.tvQty)).setText(text);
//	}
}
