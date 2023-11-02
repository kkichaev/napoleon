package com.grsoft.manager.documents;

import com.grsoft.dataobjects.impl.SPKTaskImpl;
import com.grsoft.manager.R.string;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.network.DocExportListener;

public class SPKTaskDoc extends MDocType {
	static protected SPKTaskDoc instance = null;
	private static final String OBJ_NAME = "SPKTask";
	
	protected SPKTaskDoc() {
		this(SPKTaskImpl.class);
	}
	
	protected SPKTaskDoc(Class<? extends SPKTaskImpl> type){
		super(OBJ_NAME, type);
	}

	static public SPKTaskDoc instance() {
		if( instance == null )
			instance = new SPKTaskDoc();
		return instance;
	}
	
	static public SPKTaskDoc instance(Class<? extends SPKTaskImpl> type) {
		instance = new SPKTaskDoc(type);
		return instance;
	}

	@Override
	public int getDocTitle() { return string.answer_doc_title;	}
}
