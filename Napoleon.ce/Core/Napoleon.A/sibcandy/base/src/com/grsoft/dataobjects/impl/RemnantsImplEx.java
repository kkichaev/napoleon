package com.grsoft.dataobjects.impl;

import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

import android.content.Context;

public class RemnantsImplEx extends RemnantsImpl {
	public boolean isCreatedToday() {
		return Util.getDayStart(data.date).compareTo(Util.getDate()) == 0;
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		boolean ret = super.init(context, orgId, coord);
		if(isCreatedToday() && isExported())
			setExported(false);
		return ret;
	}
	
	@Override
	public void open(Context context) {
		if(isCreatedToday() && isExported())
			setExported(false);		
		super.open(context);
	}
}
