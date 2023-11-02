package com.grsoft.database;

import com.grsoft.dataobjects.TaskInfo;


public class TaskInfoRestore extends DataObjectRestore {

	public TaskInfoRestore() {
		super(TaskInfo.class, "TaskInfo", "date");
	}

}
