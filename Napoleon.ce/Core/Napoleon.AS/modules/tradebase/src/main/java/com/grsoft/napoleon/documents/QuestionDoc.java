package com.grsoft.napoleon.documents;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.Answer;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.AnswerImpl;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.dataobjects.impl.QuestionImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.documents.CreateByScriptDef;
import com.grsoft.util.Util;

public class QuestionDoc extends DocType 
implements CreateByScriptDef{
	protected static QuestionDoc instance = null;
	private static final String DOC_NAME = "Анкеты";
	public static final String OBJ_NAME = "Answer"; 
	public static Class<? extends QuestionImpl> QuestionType = QuestionImpl.class;
	
	protected QuestionDoc() {
		super(DOC_NAME, OBJ_NAME, AnswerImpl.class);
	}
	
	protected QuestionDoc(String docName, String objName, Class<? extends AnswerImpl> type) { 
		super(docName, objName, type);
	} 
	
	static public DocType instance() {
		if( instance == null )
			instance = new QuestionDoc();
		
		Features.QUESTION = true;
		return instance;
	}
	
	static public DocType instance(Class<? extends AnswerImpl> type) {
		instance = new QuestionDoc(DOC_NAME, OBJ_NAME, type);
		return instance;
	}
	
	@Override
	public DocList docList(String orgId, String order, String dummy) {
		StringBuilder where = new StringBuilder();
		long now = Util.getDate().getTime();
		where.append("((params & 1) = 0) OR (((params & 1) = 1) AND [from]<=")
			.append(now).append(" AND [till]>=").append(now).append(")");
		
		DocList list = new DocList(QuestionType, where.toString(), "number ASC");
		return list;
	}

	@Override
	public boolean outOfScript() { return false; }

	@Override
	public int getResurceId() { 
		return R.drawable.quest_doc; 
	}
	
	@Override 
	public int getResurce2Id() { 
		return R.drawable.quest_doc_2; 
	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		View v = documentsView.findViewById(R.id.btnSendDocList);
		if( v != null )
			v.setVisibility(View.GONE);
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		TextView tvMainDocValColTitle = (TextView) documentsView
				.findViewById(com.grsoft.napoleon.R.id.tvMainDocValColTitle);
			
		if (tvMainDocValColTitle != null){
			tvMainDocValColTitle.setVisibility(View.VISIBLE);
			tvMainDocValColTitle.setText(R.string.answers);
		}
		
		TextView tvSumColumnTitle = (TextView) documentsView
			.findViewById(R.id.SumColumnTitle);
		
		if (tvSumColumnTitle != null)
			tvSumColumnTitle.setVisibility(View.GONE);
		
		TextView tvNameTitle = (TextView) documentsView
				.findViewById(R.id.NameTitle);
		
		if (tvNameTitle != null){
			tvNameTitle.setVisibility(View.VISIBLE);
			tvNameTitle.setText(R.string.title);
		}
		
		TextView tvDateTitle = (TextView) documentsView
				.findViewById(R.id.DateTitle);
		
		if (tvDateTitle != null)
			tvDateTitle.setVisibility(View.GONE);
		
		View v = documentsView.findViewById(R.id.btnSendDocList);
		if( v != null )
			v.setVisibility(View.VISIBLE);

		v = documentsView.findViewById(R.id.tvSum);

		if (v != null)
			v.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public boolean isCreatable() {
		return false;
	} 
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		String orgId = ((DocumentsAdapter)adapter).orgId;
		StringBuilder where = new StringBuilder("question=");
		where.append("'").append(((QuestionImpl)doc).getData().idquest).append("' ");
		where.append(" and id='").append(orgId).append("'");
		DocList docList = new DocList(AnswerImpl.class, where.toString(), "");
		
		TextView tv = (TextView) view.findViewById(R.id.tvOther);
		tv.setText(doc.getDescription(view.getContext()));
		
		if (docList.getCount() > 0)
			tv.setBackgroundColor(Color.GREEN);
		else
			tv.setBackgroundColor(Color.WHITE);
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setVisibility(View.GONE);
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setVisibility(View.GONE);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public DocExportListener getDirtyDocuments() {
		Document<?> d = create();
		
		if (d instanceof CreatableDocument){
			return new DocSendListner(getObjectName(), 
				(Class<? extends CreatableDocument<?>>) d.getClass(), 
				"params", ParamState.ofExported);
		} else
			return null;
	}
	
	@Override
	protected String getValueFromOrgSum(OrgSumImpl orgSumImpl) {
		StringBuilder result = new StringBuilder();
		
		Cursor<Answer> answers = new Cursor<Answer>((AnswerImpl)create(), 
				String.format("id='%s'", orgSumImpl.getData().id));
		
		result.append(answers.getCount());
		answers.close();
		
		return result.toString();
	}
	
	@Override
	public void updateTotalSum(Activity activity, int sum, int weight, int count) {
		TextView tvTotalSum = (TextView) activity.findViewById(R.id.tvSum);
		
		if (tvTotalSum != null)
			tvTotalSum.setVisibility(View.GONE);
	}

	@Override
	public Document<?> create(ScriptDef def, ScriptDefItem item) {
		AnswerImpl result = (AnswerImpl) create();
		result.getData().question = item.condParam;
		
		return result;
	}
	
	@Override
	public int getDocTitle() {
		return R.string.question_doc_title;
	}
}
