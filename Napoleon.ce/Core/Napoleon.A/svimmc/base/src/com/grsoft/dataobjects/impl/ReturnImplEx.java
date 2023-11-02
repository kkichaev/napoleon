package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.napoleon.ReturnPriceCount;
import com.grsoft.util.DocNumberHelper;
import com.grsoft.util.GpsCoord;

public class ReturnImplEx extends ReturnImpl {
		
	@Override
	public void editItem(final long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this);
	}
	
	@Override
	public boolean initSilent(String orgId, GpsCoord coord) {
		((ReturnEx)data).ordnumber = DocNumberHelper.geDocNumber(data.getClass());
		return super.initSilent(orgId, coord);
	}
}
