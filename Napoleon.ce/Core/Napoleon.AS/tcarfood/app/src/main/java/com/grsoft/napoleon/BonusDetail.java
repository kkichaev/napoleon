package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.ExtrasConst;

public class BonusDetail extends OrderDetail {
	static public void open(Context context, OrderImplBase<? extends Order> order) {
		Intent i = new Intent(context, BonusDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);		
	}
	
	@Override protected boolean haveFocusedGroup() { return false; }
}
