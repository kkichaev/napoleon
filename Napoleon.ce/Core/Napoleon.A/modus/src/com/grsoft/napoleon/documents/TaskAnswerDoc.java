package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.TaskAnswerImpl;


public class TaskAnswerDoc extends DocType {

	private static final String OBJ_NAME = "TaskAnswer";
	private static DocType instance;
	
	protected TaskAnswerDoc() {
		super(OBJ_NAME, OBJ_NAME, TaskAnswerImpl.class);
	}

	public static DocTypeBase instance() {
		if(instance == null)
			instance = new TaskAnswerDoc();
		
		return instance;
	}
}
