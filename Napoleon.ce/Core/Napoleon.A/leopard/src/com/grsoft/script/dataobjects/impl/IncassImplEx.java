package com.grsoft.script.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class IncassImplEx extends IncassImpl {
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		boolean result = super.init(context, orgId, gpsCoord);
		
		if(result){
			data.date = Util.getDateTime();
			result = (write() != ExtrasConst.INVALID_ID);
		}
		
		return result;
	}
}
