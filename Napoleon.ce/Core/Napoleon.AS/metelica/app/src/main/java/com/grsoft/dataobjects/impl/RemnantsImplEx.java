package com.grsoft.dataobjects.impl;

import com.grsoft.util.GpsCoord;

import android.content.Context;

public class RemnantsImplEx extends RemnantsImpl {
	@Override
	protected void openPrice(Context context) {
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		super.init(context, orgId, coord);
		return true;
	}
}
