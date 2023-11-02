package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.util.GpsCoord;


public class OrderImplEx extends OrderImpl {
	public interface IStopRemark { String getStopRemark(); }
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		if(context instanceof IStopRemark)
			((OrderEx)data).stpRmt = ((IStopRemark)context).getStopRemark();
		
		return super.init(context, orgId, coord);
	}
}
