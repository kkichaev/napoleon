package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.dataobjects.SPKTask;
import com.grsoft.manager.SPKTaskEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;

import android.content.Context;

public class SPKTaskImpl extends CreatableDocument<SPKTask> {

	@Override
	public void open(Context context) {
		SPKTaskEdit.open(context, getRowid());
	}
	
	public boolean init(Context context, Date start, Date finish, String agentid, GpsCoord gpsCoord){
		data.agentid = agentid;
		data.start = start;
		data.finish = finish;
		return super.init(context, "", gpsCoord);
	}
	
	@Override
	public boolean isEditable() {
		return data.status == 0;
	}

}
