package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Dispatch;
import com.grsoft.dataobjects.impl.DVisitImpl;
import com.grsoft.napoleon.R;

public class DVisitDoc extends DocTypeBase
{
	static public final String DOC_NAME = "Посещения";
	static public final String OBJ_NAME = "DVisit";
		
	protected static DVisitDoc instance = null;
	
	protected DVisitDoc() { super(DOC_NAME, OBJ_NAME, DVisitImpl.class);}

	protected DVisitDoc(String docName, String objName, Class<? extends Document<?>> type) { 
		super(docName, objName, type);
	} 
	
	public static DocTypeBase instance() {
		if( instance == null )
			instance = new DVisitDoc();
		return instance;
	}

	static public DocTypeBase instance(Class<? extends DVisitImpl> type) {
		instance = new DVisitDoc(DOC_NAME, OBJ_NAME, type);
		return instance;
	}
	
	@Override protected int getExportFlag() { return super.getExportFlag() | Dispatch.NOT_READY_TO_SEND;	}
	
	@Override public int getDocTitle() { return R.string.visit_doc_title; }
}