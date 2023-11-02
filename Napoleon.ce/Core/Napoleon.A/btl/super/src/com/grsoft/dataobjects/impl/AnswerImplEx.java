package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.AnswerEx;
import com.grsoft.napoleon.util.ConfigImpl2Ex;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.GpsCoord;

public class AnswerImplEx extends AnswerImpl {
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		AnswerEx answer = (AnswerEx) getData();
		answer.superid = ((ConfigImpl2Ex)ConfigManager.getConfig()).getSuperId();
		return super.init(context, orgId, gpsCoord);
	}
}
