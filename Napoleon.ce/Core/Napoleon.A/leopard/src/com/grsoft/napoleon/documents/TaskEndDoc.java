package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.AgentTaskImpl;
import com.grsoft.dataobjects.impl.TaskEndImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;

public class TaskEndDoc extends TaskBeginDoc {
	static public final String OBJ_NAME = "AgentTask";
	static protected TaskEndDoc instance = null;
	
	protected TaskEndDoc() {
		super(OBJ_NAME, TaskEndImpl.class);
	}
	
	static public TaskEndDoc instance() {
		if( instance == null )
			instance = new TaskEndDoc();
		
		return instance;
	}
	
	@Override
	public DocExportListener getDirtyDocuments() {
		String where = "(([params] & " + ParamState.ofExported + " ) = 0)";
		DocList AgentTaskList = new DocList(AgentTaskImpl.class, where, "");
		return new DocSendListner(OBJ_NAME, AgentTaskList);
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.task_begin;
	}
}
