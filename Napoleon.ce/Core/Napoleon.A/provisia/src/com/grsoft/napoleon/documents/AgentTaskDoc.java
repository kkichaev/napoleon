package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.AgentTaskImpl;
import com.grsoft.napoleon.R;

public class AgentTaskDoc extends DateDocType {
	static AgentTaskDoc instance = null;
	
	public static DocType instance() {
		if( instance == null )
			instance = new AgentTaskDoc();
		return instance;
	}
	
	AgentTaskDoc() {
		super("Задачи агента", "AgentTask", AgentTaskImpl.class);
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.taskdoc;
	}
}
