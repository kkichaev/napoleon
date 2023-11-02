package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.OrgTaskExecMImpl;
import com.grsoft.napoleon.R;

public class TaskDoneDocM extends DocType {
	static protected TaskDoneDocM instance = null;

	protected TaskDoneDocM() {
		super("Задачи", "TaskDone", OrgTaskExecMImpl.class);
	}

	static public TaskDoneDocM instance() {
		if (instance == null)
			instance = new TaskDoneDocM();
		return instance;
	}

	@Override
	public int getResurceId() {
		return R.drawable.taskdoc;
	}
}
