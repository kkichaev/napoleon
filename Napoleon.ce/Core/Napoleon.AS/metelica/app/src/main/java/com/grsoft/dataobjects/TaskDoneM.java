package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="orgTaskExec", keyFields="created")
public class TaskDoneM extends CreateDocDataObject{
	public List<TaskDoneItemM> items = new ArrayList<TaskDoneItemM>();
}
