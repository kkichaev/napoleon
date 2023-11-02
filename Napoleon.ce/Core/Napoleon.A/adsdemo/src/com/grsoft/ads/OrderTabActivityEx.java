package com.grsoft.ads;

import android.content.Intent;

import com.grsoft.ads.documents.AdapterListDocType;
import com.grsoft.util.ExtrasConst;

public class OrderTabActivityEx extends OrderTabActivity {
	@Override
	protected void initTabPages(AdapterListDocType curDoc, long orderid) {
		Intent odIntenet = new Intent(this, curDoc.getSummary());
		odIntenet.putExtra(ExtrasConst.DOC_ROW_ID_STR, orderid);
		
		getTabHost().addTab(getTabHost().newTabSpec(OrderSummary.TAB_NAME).setIndicator(
				curDoc.getSummaryTitle(), getResources().getDrawable(curDoc.getSummaryIndicator()))
				.setContent(odIntenet));
		
		Intent phIntent = new Intent(this, OrderPhoto.class);
		phIntent.putExtra(ExtrasConst.DOC_ROW_ID_STR, orderid);
		
		getTabHost().addTab(getTabHost().newTabSpec(OrderPhoto.TAB_NAME).setIndicator(
				OrderPhoto.TAB_CAPTION, getResources().getDrawable(R.drawable.camera))
				.setContent(phIntent));
		/*
		Intent oiIntent = new Intent(this, OrderReturn.class);
		oiIntent.putExtra(ExtrasConst.DOC_ROW_ID_STR, orderid);
		
		getTabHost().addTab(getTabHost().newTabSpec(OrderReturn.TAB_NAME).setIndicator(
				OrderReturn.TAB_CAPTION, getResources().getDrawable(R.drawable.return_doc))
				.setContent(oiIntent));
		*/
	}
}
