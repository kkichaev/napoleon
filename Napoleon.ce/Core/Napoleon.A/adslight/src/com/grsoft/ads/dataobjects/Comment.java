package com.grsoft.ads.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="comment", keyFields="taskid")
public class Comment extends DataObject {
	public String taskid = "";
	public String text = "";
}
