package com.grsoft.dataobjects.impl;

import com.grsoft.napoleon.ReturnCountEx;

import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnCountEx.open(context, itemRowid, this);
	}
}
