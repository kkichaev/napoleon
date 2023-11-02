package com.grsoft.script.dataobjects.impl;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;

import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;

public class VisitImplEx extends VisitImpl {
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		boolean result = super.init(context, orgId, gpsCoord);
		SharedPreferences pref = context.getSharedPreferences(ScriptImplEx.SCRIPT_PREF, Context.MODE_PRIVATE);
		long date = pref.getLong(ScriptImplEx.SCRIPT_DATE, Calendar.getInstance().getTimeInMillis());
		
		if(result){
			data.date = new Date(date);
			result = (write() != ExtrasConst.INVALID_ID);
		}
		
		return result;
	}
}
