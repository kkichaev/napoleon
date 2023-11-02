package com.grsoft.manager.documents;

import com.grsoft.dataobjects.impl.MVisitImpl;
import com.grsoft.manager.R.string;


public class MVisitDoc extends MDocType {
	static protected MVisitDoc instance = null;
	private static final String OBJ_NAME = "VisitInfo";
	
	protected MVisitDoc() {
		this( MVisitImpl.class);
	}
	
	protected MVisitDoc(Class<? extends MVisitImpl> type){
		super(OBJ_NAME, type);
	}

	static public MDocType instance() {
		if( instance == null )
			instance = new MVisitDoc();
		return instance;
	}
	
	static public MDocType instance(Class<? extends MVisitImpl> type) {
		instance = new MVisitDoc(type);
		return instance;
	}

	@Override
	public int getDocTitle() { return string.visit_doc_title;	}
}
