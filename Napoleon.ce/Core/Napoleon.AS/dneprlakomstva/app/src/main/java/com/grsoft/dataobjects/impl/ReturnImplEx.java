package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.napoleon.ReturnPriceCount;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this);
	}
	
	@Override
	public CreatableDocument<Return> copy() {
		return null;
	}
	
	@Override
	public String getDescription(Context context) {
		return ((ReturnEx)data).retNumber.length() > 0 ? ((ReturnEx)data).retNumber :
			super.getDescription(context);
	}
}
