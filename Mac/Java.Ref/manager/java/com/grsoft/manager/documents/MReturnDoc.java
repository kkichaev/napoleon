package com.grsoft.manager.documents;

import com.grsoft.dataobjects.impl.MReturnImpl;
import com.grsoft.manager.R.string;

public class MReturnDoc extends MDocType {
	static protected MReturnDoc instance = null;
	private static final String OBJ_NAME = "Returns";
	
	protected MReturnDoc() {
		this(MReturnImpl.class);
	}
	
	protected MReturnDoc(Class<? extends MReturnImpl> type){
		super(OBJ_NAME, type);
	}

	static public MDocType instance() {
		if( instance == null )
			instance = new MReturnDoc();
		return instance;
	}
	
	static public MDocType instance(Class<? extends MReturnImpl> type) {
		instance = new MReturnDoc(type);
		return instance;
	}

	@Override
	public int getDocTitle() { return string.return_doc_title;	}

}
