package com.grsoft.ads;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.grsoft.ads.documents.AdapterListDocType;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.ExtrasConst;

public class OrderTabActivity extends TabActivity {	
	public static Class<? extends TabActivity> orderTabActivity = OrderTabActivity.class;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		long rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, 
				ExtrasConst.INVALID_ID);  
				
		AdapterListDocType curDoc = (AdapterListDocType) DocType.getCurDoc();
		setTitle(curDoc.getTitle());
		
		initTabPages(curDoc, rowid);
	}
	
	protected void initTabPages(AdapterListDocType curDoc, long orderid){
		Intent odIntenet = new Intent(this, curDoc.getSummary());
		odIntenet.putExtra(ExtrasConst.DOC_ROW_ID_STR, orderid);
		
		getTabHost().addTab(getTabHost().newTabSpec(OrderSummary.TAB_NAME).setIndicator(
				curDoc.getSummaryTitle(), getResources().getDrawable(curDoc.getSummaryIndicator()))
				.setContent(odIntenet));
		
		if(curDoc.hasAddress()){
			Intent adIntent = new Intent(this, Address.class);
			adIntent.putExtra(ExtrasConst.DOC_ROW_ID_STR, orderid);
			
			getTabHost().addTab(getTabHost().newTabSpec(Address.TAB_NAME).setIndicator(
					Address.TAB_CAPTION, getResources().getDrawable(R.drawable.client))
					.setContent(adIntent));
		}
		
		Intent oiIntent = new Intent(this, OrderDetail.class);
		oiIntent.putExtra(ExtrasConst.DOC_ROW_ID_STR, orderid);
		
		getTabHost().addTab(getTabHost().newTabSpec(OrderDetail.TAB_NAME).setIndicator(
				OrderDetail.TAB_CAPTION, getResources().getDrawable(R.drawable.material))
				.setContent(oiIntent));
	}
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, orderTabActivity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
}
