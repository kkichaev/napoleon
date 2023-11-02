package com.grsoft.dataobjects.impl;

import com.grsoft.napoleon.ReturnPriceCount;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.util.GpsCoord;

import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this);
	}
}
