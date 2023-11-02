package com.grsoft.dataobjects.impl;
import android.content.Context;

import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.Return;
import com.grsoft.napoleon.ReturnPriceCount;
import com.grsoft.napoleon.documents.CreatableDocument;

public class ReturnImpl extends ReturnImplBase<Return> {

	@Override public CreatableDocument<Return> createInstance() { 
		return new ReturnImpl(); 
	}

	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this);
	}
}
