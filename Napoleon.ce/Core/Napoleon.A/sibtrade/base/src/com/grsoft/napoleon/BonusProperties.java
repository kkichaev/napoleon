package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.BonusImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.ExtrasConst;


public class BonusProperties extends CreateOrder {

	public static void open(Context context, OrderImplBase<? extends Order> order, boolean editOldOrder) {
		Intent i = new Intent(context, BonusProperties.class);
		
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());

		context.startActivity(i);		
	}
	
	@Override protected int getLayoutId() { return R.layout.createbonus; }

	@Override
	protected OrderImplBase<? extends Order> createDocument() { return new BonusImpl(); }
}
