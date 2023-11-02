package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.RkoImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;


public class RkoDoc extends DocType {
	private static RkoDoc instance;
	
	protected RkoDoc() {
		super("– Œ", "Rko", RkoImpl.class);
	}
	
	public static DocType instance() {
		if( instance == null )
			instance = new RkoDoc();
		return instance;
	}
	
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

	@Override public int getResurceId() { return R.drawable.rko_doc; }
}
