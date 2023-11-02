package com.grsoft.dataobjects.impl;

import com.grsoft.napoleon.CreateReturn;
import com.grsoft.napoleon.ReturnPriceCount;
import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this);
	}
	
	@Override public void editProperties(Context context, boolean isOldOrder) {
		CreateReturn.open(context, this, isOldOrder);
	}
	
//	@Override
//	public boolean init(Context context, String orgId, GpsCoord coord) {
//		super.init(context, orgId, coord);
////		Warehouse.open(context, this, false);
//		return false;
//	}
}
