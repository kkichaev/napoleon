package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.VisitEx;
import com.grsoft.napoleon.util.ConfigImpl2Ex;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.GpsCoord;

public class VisitImpl2Ex extends VisitImplEx{
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		VisitEx visit = (VisitEx)getData();
		visit.superid = ((ConfigImpl2Ex)ConfigManager.getConfig()).getSuperId();
		return super.init(context, orgId, gpsCoord);
	}
}
