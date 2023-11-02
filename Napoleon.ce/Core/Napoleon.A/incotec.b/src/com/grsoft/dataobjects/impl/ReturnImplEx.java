package com.grsoft.dataobjects.impl;

import com.grsoft.napoleon.ReturnCount;
import com.grsoft.napoleon.ReturnProperties;

import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editProperties(Context context, boolean isOldOrder) {
		ReturnProperties.open(context, this, isOldOrder);
	}
	
	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnCount.open(context, this, itemRowid);
	}
}
