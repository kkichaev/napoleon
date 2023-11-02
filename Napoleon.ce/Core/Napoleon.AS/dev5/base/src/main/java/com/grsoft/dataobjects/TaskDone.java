package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="orgTaskExec", keyFields="created")
public class TaskDone extends CreateDocDataObject{
	public String idTask = "";
	
	public List<TaskDoneItem> items = new ArrayList<TaskDoneItem>();
}
