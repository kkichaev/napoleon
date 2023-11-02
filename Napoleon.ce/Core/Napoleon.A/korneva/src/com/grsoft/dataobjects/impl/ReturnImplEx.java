package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Return;
import com.grsoft.napoleon.PriceCount;

import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCount.open(context, itemRowid, (DbObject<Return>)this); 
	}
}
