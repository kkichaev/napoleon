package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.TaskDone;
import com.grsoft.dataobjects.TaskDoneItem;
import com.grsoft.napoleon.OrgTaskList;
import com.grsoft.napoleon.documents.CreatableDocument;

public class OrgTaskExecImplW extends CreatableDocument<TaskDone> {
	private OrgTaskDoneImpl taskDoneImpl = new OrgTaskDoneImpl();

	@Override
	public void open(Context context) {
		OrgTaskList.open(context, getId(), getRowid());
	}

	public void recheckItem(String id) {
		boolean c = false;
		taskDoneImpl.getData().id = id;
		taskDoneImpl.read();

		for (TaskDoneItem i : data.items)
			if (i.id.equals(id)) {
				c = true;
				i.done ^= 1;
				
				if(i.done == 0)
					taskDoneImpl.delete();
				else
					taskDoneImpl.write();
				
				break;
			}

		if (!c) {
			TaskDoneItem i = new TaskDoneItem();
			i.id = id;
			i.done = 1;
			data.items.add(i);
			taskDoneImpl.write();
		}

		write();
		close();
		taskDoneImpl.close();
	}

	public boolean isTaskDone(String id) {
		boolean result = false;

		for (TaskDoneItem i : data.items)
			if (i.id.equals(id)) {
				result = i.done > 0;
				break;
			}

		return result;
	}

	public void inputRemark(String id, String text) {
		boolean c = false;

		for (TaskDoneItem i : data.items)
			if (i.id.equals(id)) {
				c = true;
				i.text = text;
				break;
			}

		if (!c) {
			TaskDoneItem i = new TaskDoneItem();
			i.id = id;
			i.text = text;
			data.items.add(i);
		}

		write();
		close();
	}

	public String getRemark(String id) {
		String result = "";

		for (TaskDoneItem i : data.items)
			if (i.id.equals(id)) {
				result = i.text;
				break;
			}

		return result;
	}
}
