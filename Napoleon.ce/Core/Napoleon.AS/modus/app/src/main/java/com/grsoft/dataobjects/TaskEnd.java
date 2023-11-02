package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.TableInfo;

@TableInfo(name="taskend", keyFields="created")
public class TaskEnd extends CreateDocDataObject {
	public List<TaskItem> newtasks = new ArrayList<TaskItem>();
	public List<TaskItem> exectasks = new ArrayList<TaskItem>();
}
