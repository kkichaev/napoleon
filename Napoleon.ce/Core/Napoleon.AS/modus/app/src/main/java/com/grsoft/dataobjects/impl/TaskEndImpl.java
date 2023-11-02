package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.TaskEnd;
import com.grsoft.dataobjects.TaskItem;
import com.grsoft.napoleon.TaskChecker;
import com.grsoft.napoleon.documents.CreatableDocument;


public class TaskEndImpl extends CreatableDocument<TaskEnd> {

	@Override
	public void open(Context context) { TaskChecker.open(context, getRowid());}

	public void putTask(String taskid) {
		TaskItem i = new TaskItem();
		i.id = taskid;
		data.newtasks.add(i);
	}

	public void delTask(String taskid) {
		for(TaskItem i : data.newtasks)
			if(i.id.equals(taskid)){
				data.newtasks.remove(i);
				break;
			}
	}
	
	public void execTask(String taskid){
		TaskItem i = new TaskItem();
		i.id = taskid;
		data.exectasks.add(i);
	}
	
	public void delExecTask(String taskid) {
		for(TaskItem i : data.exectasks)
			if(i.id.equals(taskid)){
				data.exectasks.remove(i);
				break;
			}
	}

}
