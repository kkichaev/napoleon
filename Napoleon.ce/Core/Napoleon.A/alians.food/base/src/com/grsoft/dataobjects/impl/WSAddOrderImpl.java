package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.WSAddOrder;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.PriceCountEx;
import com.grsoft.napoleon.WSOrderDetail;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class WSAddOrderImpl extends OrderImplBase<WSAddOrder>{

	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCount.open(context, itemRowid, this);
	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		Warehouse.open(ctx, this, isOldOrder);
	}

	@Override
	public CreatableDocument<WSAddOrder> createInstance() {
		return new WSAddOrderImpl();
	}

	@Override
	public void open(Context context) {
		WSOrderDetail.open(context, this);
	}
	
	@Override
	protected boolean checkPriceQty() {
		return false;
	}
	
	public void editItemMode(long itemRowid, Context context) {
		PriceCountEx.openEditMode(context, itemRowid, this);
	}

}
