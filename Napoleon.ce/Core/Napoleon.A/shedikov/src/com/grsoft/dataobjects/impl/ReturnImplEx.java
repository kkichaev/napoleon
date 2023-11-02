package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.napoleon.PriceCountW;


public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editItem(final long itemRowid, final Context context) {
		PriceCountW.open(context, itemRowid, (DbObject<?>)this);
	}
}
