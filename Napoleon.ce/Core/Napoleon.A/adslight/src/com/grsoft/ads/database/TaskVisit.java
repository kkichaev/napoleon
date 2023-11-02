package com.grsoft.ads.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.CreateDocDataObject;

@TableInfo(name="TaskVisit", keyFields="created")
@ServerInfo(name="TaskVisit")
public class TaskVisit extends CreateDocDataObject{
	public String taskid = "";
	public int readytosend = 0;
	public Date done;
	
	public List<TaskVisitItem> items = new ArrayList<TaskVisitItem>();
}
