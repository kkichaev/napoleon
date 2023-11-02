package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="TaskRemark", keyFields="taskid")
public class TaskRemark extends DataObject{
	public String taskid = "";
	public String remark = "";
	public int params = 0;
	public Date date;
}
