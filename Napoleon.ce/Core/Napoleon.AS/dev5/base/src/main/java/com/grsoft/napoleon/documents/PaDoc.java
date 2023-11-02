package com.grsoft.napoleon.documents;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.PaImpl;
import com.grsoft.aceteam.R;
import com.grsoft.network.DocExportListener;

public class PaDoc extends DocType {
	static public final String DOC_NAME = "Доверенность";
	static public final String OBJ_NAME = "PA";
	private static DocType instance;
	
	protected PaDoc() {
		super(DOC_NAME, OBJ_NAME,  PaImpl.class);
	}
	
	public static DocType instance() {
		if( instance == null )
			instance = new PaDoc();
		return instance;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.pa;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DocExportListener getDirtyDocuments() {
		CreatableDocument<?> d = (CreatableDocument<?>)create();
		return new DocSendListner(getObjectName(), 
				(Class<? extends CreatableDocument<?>>) d.getClass(), 
				"params", ParamState.ofExported);
	}
}
