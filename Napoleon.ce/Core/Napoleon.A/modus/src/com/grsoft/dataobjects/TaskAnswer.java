package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="TaskAnswer", keyFields="taskid")
public class TaskAnswer extends CreateDocDataObject {
	public String taskid = "";
}
