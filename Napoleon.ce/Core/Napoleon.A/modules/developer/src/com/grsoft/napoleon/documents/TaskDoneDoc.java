package com.grsoft.napoleon.documents;

import android.app.Activity;
import com.grsoft.dataobjects.impl.OrgTaskExecImplW;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;

public class TaskDoneDoc extends TaskDoneDocW {

	static public TaskDoneDocW instance() {
		return instance(OrgTaskExecImplW.class);
	}
	
	protected TaskDoneDoc(Class<? extends OrgTaskExecImplW> doc) {
		super(doc);
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
	
	@Override
	public void updateTotalSum(Activity activity, long sum, int weight, int count, int textViewId) {
	}
}
