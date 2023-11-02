package com.grsoft.ads;

import android.content.Intent;

import com.grsoft.ads.documents.AdapterListDocType;
import com.grsoft.util.ExtrasConst;

public class OrderTabActivityEx extends OrderTabActivity {
	@Override
	protected void initTabPages(AdapterListDocType curDoc, long orderid) {
		super.initTabPages(curDoc, orderid);
		
		Intent oiIntent = new Intent(this, OrderData.class);
		oiIntent.putExtra(ExtrasConst.DOC_ROW_ID_STR, orderid);
		
		getTabHost().addTab(getTabHost().newTabSpec(OrderData.TAB_NAME).setIndicator(
				OrderData.TAB_CAPTION, getResources().getDrawable(R.drawable.orderdata))
				.setContent(oiIntent));
	}
}
