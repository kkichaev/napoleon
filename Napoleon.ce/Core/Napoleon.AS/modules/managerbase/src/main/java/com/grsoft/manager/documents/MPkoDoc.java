package com.grsoft.manager.documents;

import com.grsoft.dataobjects.impl.MPkoImpl;
import com.grsoft.manager.R.string;

public class MPkoDoc extends MDocType {
	static protected MPkoDoc instance = null;
	private static final String OBJ_NAME = "Pko";
	
	protected MPkoDoc() {
		this(MPkoImpl.class);
	}
	
	protected MPkoDoc(Class<? extends MPkoImpl> type){
		super(OBJ_NAME, type);
	}

	static public MDocType instance() {
		if( instance == null )
			instance = new MPkoDoc();
		return instance;
	}
	
	static public MDocType instance(Class<? extends MPkoImpl> type) {
		instance = new MPkoDoc(type);
		return instance;
	}

	@Override
	public int getDocTitle() { return string.incass_doc_title;	}

}
