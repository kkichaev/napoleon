package com.grsoft.ads.dataobjects.impl;

import com.grsoft.ads.dataobjects.TaskResponce;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.napoleon.dataobjects.TaskQuery;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.gps.GPSUtilNew;
import android.content.Context;


public class TaskResponceImpl extends CreatableDocument<TaskResponce> {

	@Override
	public void open(Context context) {}

	public boolean init(Context context, TaskQuery src){
		data.taskid = src.taskid;
		DbWriter.checkDBTable(data.getClass());
		DbReader r = new DbReader();
		
		TaskResponce tr = new TaskResponce(); 
		if (r.select(tr, table, String.format("taskid='%s'",src.taskid), "created desc"))
			data.lasttime = tr.created;
		
		return super.init(context, "", GPSUtilNew.getLastKnownLocation());
	}
}
