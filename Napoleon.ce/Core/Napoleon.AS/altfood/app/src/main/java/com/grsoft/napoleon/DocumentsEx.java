package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class DocumentsEx extends Documents {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		findViewById(R.id.btnPrice).setOnClickListener(btnPriceClick);
	}
	
	OnClickListener btnPriceClick = new OnClickListener() { @Override public void onClick(View v) { WarehouseEx.open(v.getContext(), org.getData().costype);} };
	
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}
}
