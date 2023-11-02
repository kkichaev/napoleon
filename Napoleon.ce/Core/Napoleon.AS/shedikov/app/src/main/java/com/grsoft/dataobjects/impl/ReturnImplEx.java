package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.napoleon.PriceCount;


public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editItem(final long itemRowid, final Context context) {
		PriceCount.open(context, itemRowid, (DbObject<?>)this);
	}
}
