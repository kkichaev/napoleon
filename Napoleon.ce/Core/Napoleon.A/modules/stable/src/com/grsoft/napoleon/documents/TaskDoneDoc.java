package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;

public class TaskDoneDoc extends TaskDoneDocW {

	protected TaskDoneDoc(Class<? extends OrgTaskExecImpl> doc) {
		super(doc);
	}

//	static public TaskDoneDocW instance(Class<? extends OrgTaskExecImpl> doc) {
//		if (instance == null) {
//			instance = new TaskDoneDocW(doc);
//			Features.ORG_TASK = true;
//			ScriptDefImpl.docInScript.add(instance);
//		}
//		return instance;
//	}
}
