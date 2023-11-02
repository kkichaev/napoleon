package com.grsoft.dataobjects.impl;

import com.grsoft.napoleon.PriceCount;

import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCount.open(context, itemRowid, this);
	}
}
