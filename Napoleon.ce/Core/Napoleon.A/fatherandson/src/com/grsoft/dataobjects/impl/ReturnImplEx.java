package com.grsoft.dataobjects.impl;

import com.grsoft.napoleon.PriceCountEx;

import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCountEx.open(context, itemRowid, this);
	}
}
