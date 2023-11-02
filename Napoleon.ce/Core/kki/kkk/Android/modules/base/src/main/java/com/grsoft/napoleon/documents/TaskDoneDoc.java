package com.grsoft.napoleon.documents;

import android.app.Activity;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.Answer;
import com.grsoft.dataobjects.impl.AnswerImpl;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.dataobjects.impl.OrgTaskExecImplW;
import com.grsoft.napoleon.Documents;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;

public class TaskDoneDoc extends DateDocType {
	static protected TaskDoneDoc instance = null;

	static public TaskDoneDoc instance() {
		return instance(OrgTaskExecImplW.class);
	}

	protected TaskDoneDoc(Class<? extends OrgTaskExecImplW> doc) {
		super("Задачи", "TaskDone", doc);
	}

	static public TaskDoneDoc instance(Class<? extends OrgTaskExecImplW> doc) {
		if (instance == null) {
			instance = new TaskDoneDoc(doc);
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

	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);
	}

	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);

		TextView tvSum = view.findViewById(R.id.tvSum);
		tvSum.setVisibility(View.VISIBLE);
	}

	@Override
	protected String getValueFromOrgSum(OrgSumImpl orgSumImpl) {
		int count = Documents.pendingTaskCount(orgSumImpl.getData().id);

		return count > 0 ? Integer.toString(count) : "";
	}
}
