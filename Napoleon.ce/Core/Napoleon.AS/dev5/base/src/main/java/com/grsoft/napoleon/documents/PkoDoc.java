package com.grsoft.napoleon.documents;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.PkoImpl;
import com.grsoft.aceteam.R;
import com.grsoft.network.DocExportListener;

public class PkoDoc extends DocType{

	static public final String DOC_NAME = "ойн";
	static public final String OBJ_NAME = "Pko";
	private static DocType instance;
	
	protected PkoDoc() {
		this(PkoImpl.class);
	}
	
	protected PkoDoc(Class<? extends PkoImpl> doc) {
		super(DOC_NAME, OBJ_NAME,  doc);
	}

	public static DocType instance() {
		if( instance == null )
			instance = new PkoDoc();
		return instance;
	}
	
	public static DocType instance(Class<? extends PkoImpl> doc) {
		if( instance == null )
			instance = new PkoDoc(doc);
		return instance;
	}

	@Override public int getResurceId() {
		return R.drawable.pko;
	}
	@Override public int getResurce2Id() { return R.drawable.pko_2; }

	@SuppressWarnings("unchecked")
	@Override
	public DocExportListener getDirtyDocuments() {
		CreatableDocument<?> d = (CreatableDocument<?>)create();
		return new DocSendListner(getObjectName(), 
				(Class<? extends CreatableDocument<?>>) d.getClass(), 
				"params", ParamState.ofExported);
	}
	
	@Override
	public void refreshDocSum(String orgId) {
		super.refreshDocSum(orgId);
		DebtDoc.instance().refreshDocSum(orgId);
	}
}
