package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.SVTaskImpl;
import com.grsoft.dataobjects.impl.TaskBeginImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;

public class TaskBeginDoc extends DocType {
	static public final String DOC_NAME = "Задачи";
	static public final String OBJ_NAME = "SVTask";
	static protected TaskBeginDoc instance = null;
	public final static int DONE = 1;
	
	protected TaskBeginDoc() {
		super(DOC_NAME, OBJ_NAME, TaskBeginImpl.class);
	}
	
	protected TaskBeginDoc(String objName, Class<? extends Document<?>> docClass) {
		super(DOC_NAME, objName, docClass);
	}
	
	static public TaskBeginDoc instance() {
		if( instance == null )
			instance = new TaskBeginDoc();
		return instance;
	}
	
	@Override
	public DocExportListener getDirtyDocuments() {
		String where = "(([params] & " + ParamState.ofExported + " ) = 0)";
		DocList SVTaskList = new DocList(SVTaskImpl.class, where, "");
		
		return new DocSendListner(OBJ_NAME, SVTaskList);
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.task_begin;
	}
}
