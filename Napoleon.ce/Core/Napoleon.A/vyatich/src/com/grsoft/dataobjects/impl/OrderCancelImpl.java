package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.OrderCancel;
import com.grsoft.napoleon.documents.CreatableDocument;

public class OrderCancelImpl extends OrderImplBase<OrderCancel> {

	@Override
	public void editItem(long itemRowid, Context context) {
	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
	}

	@Override
	public CreatableDocument<OrderCancel> createInstance() {
		return new OrderCancelImpl();
	}

	@Override
	public void open(Context context) {
	}

}
