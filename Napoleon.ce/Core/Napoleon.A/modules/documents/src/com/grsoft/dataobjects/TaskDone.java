package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="orgTaskExec", keyFields="created")
public class TaskDone extends CreateDocDataObject{
	public String idTask = "";
	
	public List<TaskDoneItem> items = new ArrayList<TaskDoneItem>();
}
