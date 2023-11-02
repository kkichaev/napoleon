package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.TaskBeginImpl;
import com.grsoft.napoleon.R;

public class TaskBeginDoc extends DocType {
	private static String OBJ_NAME = "TaskBegin";
	private static DocType instance;
	
	protected TaskBeginDoc() {
		super(OBJ_NAME, OBJ_NAME, TaskBeginImpl.class);
	}

	public static DocTypeBase instance() {
		if(instance == null)
			instance = new TaskBeginDoc();
		
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.task_doc; }

	@Override
	public int getResurce2Id() {
		return R.drawable.task_doc_2;
	}
}
