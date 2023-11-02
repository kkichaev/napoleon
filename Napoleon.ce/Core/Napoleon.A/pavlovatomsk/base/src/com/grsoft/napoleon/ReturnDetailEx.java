package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.ExtrasConst;

public class ReturnDetailEx extends ReturnDetail {
	static public void open(Context context, OrderImplBase<? extends Order> order) {
		Intent i = new Intent(context, ReturnDetailEx.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);		
	}
	
	@Override protected boolean haveFocusedGroup() { return false; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnAddItems.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { ReturnPriceList.open(ReturnDetailEx.this, (ReturnImplEx)doc); }
		});
	}

}
