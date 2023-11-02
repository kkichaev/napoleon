package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Return;
import com.grsoft.napoleon.ReturnPriceCount;
import com.grsoft.napoleon.ReturnProperties;
import com.grsoft.napoleon.documents.CreatableDocument;

public class ReturnImplEx extends ReturnImpl {

	@Override public CreatableDocument<Return> createInstance() { return new ReturnImplEx(); }

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		ReturnProperties.open(ctx, this, isOldOrder);
	}
	
	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this);
	}
}
