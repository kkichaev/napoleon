package com.grsoft.manager.documents;

import com.grsoft.dataobjects.impl.MAnswerImpl;
import com.grsoft.manager.R.string;

public class MAnswerDoc extends MDocType {
	static protected MAnswerDoc instance = null;
	private static final String OBJ_NAME = "MAnswer";
	
	protected MAnswerDoc() {
		this(MAnswerImpl.class);
	}
	
	protected MAnswerDoc(Class<? extends MAnswerImpl> type){
		super(OBJ_NAME, type);
	}

	static public MDocType instance() {
		if( instance == null )
			instance = new MAnswerDoc();
		return instance;
	}
	
	static public MDocType instance(Class<? extends MAnswerImpl> type) {
		instance = new MAnswerDoc(type);
		return instance;
	}

	@Override
	public int getDocTitle() { return string.answer_doc_title;	}
}
