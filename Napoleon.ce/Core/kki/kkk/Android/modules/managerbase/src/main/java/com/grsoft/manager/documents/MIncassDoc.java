package com.grsoft.manager.documents;

import com.grsoft.dataobjects.impl.MIncassImpl;
import com.grsoft.manager.R.string;

public class MIncassDoc extends MDocType {
	static protected MIncassDoc instance = null;
	private static final String OBJ_NAME = "Incass";
	
	protected MIncassDoc() {
		this(MIncassImpl.class);
	}
	
	protected MIncassDoc(Class<? extends MIncassImpl> type){
		super(OBJ_NAME, type);
	}

	static public MDocType instance() {
		if( instance == null )
			instance = new MIncassDoc();
		return instance;
	}
	
	static public MDocType instance(Class<? extends MIncassImpl> type) {
		instance = new MIncassDoc(type);
		return instance;
	}

	@Override
	public int getDocTitle() { return string.incass_doc_title;	}
}
