package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.ReturnPriceCount;

import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this);
	}
}
