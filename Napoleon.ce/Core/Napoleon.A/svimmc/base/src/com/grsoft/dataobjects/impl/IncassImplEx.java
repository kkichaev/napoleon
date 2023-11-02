package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.IncassEx;
import com.grsoft.util.DocNumberHelper;
import com.grsoft.util.GpsCoord;

public class IncassImplEx extends IncassImpl {
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		((IncassEx)data).number = DocNumberHelper.geDocNumber(data.getClass());
		return super.init(context, orgId, gpsCoord);
	}
}
