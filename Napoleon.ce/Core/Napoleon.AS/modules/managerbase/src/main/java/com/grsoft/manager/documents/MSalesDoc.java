package com.grsoft.manager.documents;

import com.grsoft.dataobjects.impl.MSalesImpl;
import com.grsoft.manager.R.string;

public class MSalesDoc extends MDocType {
	static protected MSalesDoc instance = null;
	private static final String OBJ_NAME = "Sales";
	
	protected MSalesDoc() {
		this(MSalesImpl.class);
	}
	
	protected MSalesDoc(Class<? extends MSalesImpl> type){
		super(OBJ_NAME, type);
	}

	static public MDocType instance() {
		if( instance == null )
			instance = new MSalesDoc();
		return instance;
	}
	
	static public MDocType instance(Class<? extends MSalesImpl> type) {
		instance = new MSalesDoc(type);
		return instance;
	}

	@Override
	public int getDocTitle() { return string.sales_doc_title;	}

}
