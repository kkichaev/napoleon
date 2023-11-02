package com.grsoft.manager.documents;

import com.grsoft.dataobjects.impl.MOrderImpl;
import com.grsoft.manager.R.string;


public class MOrderDoc extends MDocType{
	static protected MOrderDoc instance = null;
	public static final String OBJ_NAME = "Order";
	
	protected MOrderDoc() {
		this( MOrderImpl.class);
	}
	
	protected MOrderDoc(Class<? extends MOrderImpl> type){
		super(OBJ_NAME, type);
	}

	static public MDocType instance() {
		if( instance == null )
			instance = new MOrderDoc();
		return instance;
	}
	
	static public MDocType instance(Class<? extends MOrderImpl> type) {
		instance = new MOrderDoc(type);
		return instance;
	}

	@Override
	public int getDocTitle() { return string.order_doc_title;	}
}
