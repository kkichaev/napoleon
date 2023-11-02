package com.grsoft.ads.documents;

import com.grsoft.ads.dataobjects.impl.QuestAnswerImpl;
import com.grsoft.ads.dataobjects.impl.QuestionImpl;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.Util;

public class QuestionDoc extends DocTypeBase {
	protected static QuestionDoc instance = null;
	private static final String DOC_NAME = "Анкеты";
	private static final String OBJ_NAME = "Answer"; 
	
	protected QuestionDoc() {
		super(DOC_NAME, OBJ_NAME, QuestAnswerImpl.class);
	}
	
	protected QuestionDoc(String docName, String objName, Class<? extends QuestAnswerImpl> type) { 
		super(docName, objName, type);
	} 
	
	static public DocTypeBase instance() {
		if( instance == null )
			instance = new QuestionDoc();
		
		return instance;
	}
	
	static public DocTypeBase instance(Class<? extends QuestAnswerImpl> type) {
		instance = new QuestionDoc(DOC_NAME, OBJ_NAME, type);
		return instance;
	}
	
	@Override
	public DocList docList(String orgId, String order, String dummy) {
		StringBuilder where = new StringBuilder();
		long now = Util.getDate().getTime();
		where.append("((params & 1) = 0) OR (((params & 1) = 1) AND [from]<=")
			.append(now).append(" AND [till]>=").append(now).append(")");
		
		DocList list = new DocList(QuestionImpl.class, where.toString(), "number ASC");
		return list;
	}
	
	@Override
	public boolean isCreatable() {
		return false;
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
}
