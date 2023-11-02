package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.TaskEndImpl;
import com.grsoft.napoleon.R;


public class TaskEndDoc extends DocType {
	private static final String OBJ_NAME = "TaskEnd";
	private static DocType instance;
	
	protected TaskEndDoc() {
		super(OBJ_NAME, OBJ_NAME, TaskEndImpl.class);
	}
	
	public static DocTypeBase instance() {
		if(instance == null)
			instance = new TaskEndDoc();
		
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.task_doc; }

	@Override
	public int getResurce2Id() {
		return R.drawable.task_doc_2;
	}
}
