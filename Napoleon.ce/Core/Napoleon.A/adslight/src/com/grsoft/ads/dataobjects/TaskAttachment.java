package com.grsoft.ads.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="TaskAttachment", keyFields="id",indexes="taskid")
public class TaskAttachment extends DataObject {
	public String id = "";
	public String taskid = "";
	public String name = "";
	public String path = "";
}
