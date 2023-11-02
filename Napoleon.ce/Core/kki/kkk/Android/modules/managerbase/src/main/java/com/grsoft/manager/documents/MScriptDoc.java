package com.grsoft.manager.documents;

import com.grsoft.dataobjects.impl.MScriptImpl;
import com.grsoft.manager.R.string;

public class MScriptDoc extends MDocType {
	static protected MScriptDoc instance = null;
	private static final String OBJ_NAME = "ScriptDoc";
	
	protected MScriptDoc() {
		this(MScriptImpl.class);
	}
	
	protected MScriptDoc(Class<? extends MScriptImpl> type){
		super(OBJ_NAME, type);
	}

	static public MDocType instance() {
		if( instance == null )
			instance = new MScriptDoc();
		return instance;
	}
	
	static public MDocType instance(Class<? extends MScriptImpl> type) {
		instance = new MScriptDoc(type);
		return instance;
	}

	@Override
	public int getDocTitle() { return string.script_doc_title;	}

}
