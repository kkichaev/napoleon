package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.TaskDoneM;
import com.grsoft.dataobjects.TaskDoneItemM;
import com.grsoft.napoleon.OrgTaskListM;
import com.grsoft.napoleon.documents.CreatableDocument;

public class OrgTaskExecMImpl extends CreatableDocument<TaskDoneM> {
	private OrgTaskDoneMImpl taskDoneImpl = new OrgTaskDoneMImpl();

	@Override
	public void open(Context context) {
		OrgTaskListM.open(context, getId(), getRowid());
	}

	public void recheckItem(String id) {
		boolean c = false;
		taskDoneImpl.getData().id = id;
		taskDoneImpl.read();

		for (TaskDoneItemM i : data.items)
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
			TaskDoneItemM i = new TaskDoneItemM();
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

		for (TaskDoneItemM i : data.items)
			if (i.id.equals(id)) {
				result = i.done > 0;
				break;
			}

		return result;
	}

	public void inputRemark(String id, String text) {
		boolean c = false;

		for (TaskDoneItemM i : data.items)
			if (i.id.equals(id)) {
				c = true;
				i.text = text;
				break;
			}

		if (!c) {
			TaskDoneItemM i = new TaskDoneItemM();
			i.id = id;
			i.text = text;
			data.items.add(i);
		}

		write();
		close();
	}

	public String getRemark(String id) {
		String result = "";

		for (TaskDoneItemM i : data.items)
			if (i.id.equals(id)) {
				result = i.text;
				break;
			}

		return result;
	}
}
