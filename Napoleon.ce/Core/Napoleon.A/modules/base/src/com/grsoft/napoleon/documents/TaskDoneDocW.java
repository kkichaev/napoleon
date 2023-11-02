package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.OrgTaskExecImplW;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;

public class TaskDoneDocW extends DateDocType {
	static protected TaskDoneDocW instance = null;

	static public TaskDoneDocW instance() {
		return instance(OrgTaskExecImplW.class);
	}
	
	protected TaskDoneDocW(Class<? extends OrgTaskExecImplW> doc) {
		super("Задачи", "TaskDone", doc);
	}

	static public TaskDoneDocW instance(Class<? extends OrgTaskExecImplW> doc) {
		if (instance == null) {
			instance = new TaskDoneDocW(doc);
			Features.ORG_TASK = true;
			ScriptDefImpl.docInScript.add(instance);
		}
		return instance;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.task_doc;
	}
	
	@Override
	public int getResurce2Id() {
		return R.drawable.task_doc_2;
	}
	
	@Override
	public int getDocTitle() {
		return R.string.task_doc_title;
	}
}
