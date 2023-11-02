package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.napoleon.ReturnPriceCount;

public class ReturnImplEx extends ReturnImpl {
		
	@Override
	public void editItem(final long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this);
	}
}
