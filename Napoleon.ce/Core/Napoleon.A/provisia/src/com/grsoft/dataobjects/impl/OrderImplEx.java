package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.TimeZone;

import android.content.Context;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.util.GpsCoord;

public class OrderImplEx extends OrderImpl {
	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		Date d = new Date();
		TimeZone tz = TimeZone.getDefault();
		int off = tz.getOffset(d.getTime());
		((OrderEx)data).timeZone = - off / (1000 * 60);
		
		return super.init(context, orgId, coord); 
	}
}
