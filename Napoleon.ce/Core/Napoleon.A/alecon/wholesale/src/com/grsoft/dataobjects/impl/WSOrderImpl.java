package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.WSOrder;
import com.grsoft.napoleon.CreateOrder;
import com.grsoft.napoleon.OrderDetail;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.documents.CreatableDocument;

public class WSOrderImpl extends OrderImplBase<WSOrder> {

	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCount.open(context, itemRowid, (DbObject<WSOrder>)this); 
	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		CreateOrder.open(ctx, this, isOldOrder);
	}

	@Override
	public CreatableDocument<WSOrder> createInstance() {
		return new WSOrderImpl();
	}

	@Override
	public void open(Context context) {
		OrderDetail.open(context, this);
	}
	
	@Override
	public long sum() {
		return 0;
	}
}
