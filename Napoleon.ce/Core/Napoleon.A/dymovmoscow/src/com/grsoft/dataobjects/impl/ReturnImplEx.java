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
	
	@Override public void editProperties(Context context, boolean isOldOrder) {}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		super.init(context, orgId, coord);
		Warehouse.open(context, this, false);
		return false;
	}
}
