package com.grsoft.dataobjects.impl;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;

import com.grsoft.dataobjects.AgentTask;
import com.grsoft.napoleon.AgentTaskEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;

public class AgentTaskImpl extends CreatableDocument<AgentTask> {

	@Override
	public void open(Context context) {
		AgentTaskEdit.open(context, this);
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 7);
		data.appointDate = c.getTime();
		
		return super.init(context, orgId, gpsCoord);
	}
	
	@Override
	public String getDescription(Context context) {
		return data.category;
	}
	
	@Override
	public Date getDate() {
		return data.appointDate;
	}
}
