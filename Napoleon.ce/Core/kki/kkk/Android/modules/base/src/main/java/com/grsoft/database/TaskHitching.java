package com.grsoft.database;

import com.grsoft.dataobjects.Task;
import com.grsoft.dataobjects.impl.DbObject;

public class TaskHitching extends Hitching {

	public TaskHitching() {
		super(DbObject.getDataType(Task.class), "AgentOrgTask");
	}
}
