package com.grsoft.ads.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="taskattachmentinfo", keyFields="id", indexes="taskid")
public class TaskAttachmentInfo extends DataObject {
	public String id = "";
	public String taskid = "";
	public String name = "";
}
