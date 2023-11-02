package com.grsoft.ads.dataobjects;

import java.util.Date;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.CreateDocDataObject;

@TableInfo(name="TaskResponce", keyFields="created")
public class TaskResponce extends CreateDocDataObject {
	public String taskid = "";
	public int solution = 0;
	public Date lasttime;
}
