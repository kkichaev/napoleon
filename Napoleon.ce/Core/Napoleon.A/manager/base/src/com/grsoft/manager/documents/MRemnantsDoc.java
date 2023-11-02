package com.grsoft.manager.documents;

import com.grsoft.dataobjects.impl.MRemnantsImpl;
import com.grsoft.manager.R.string;


public class MRemnantsDoc extends MDocType {
	static protected MRemnantsDoc instance = null;
	private static final String OBJ_NAME = "OrgRemnants";
	
	protected MRemnantsDoc() {
		this( MRemnantsImpl.class);
	}
	
	protected MRemnantsDoc(Class<? extends MRemnantsImpl> type){
		super(OBJ_NAME, type);
	}

	static public MDocType instance() {
		if( instance == null )
			instance = new MRemnantsDoc();
		return instance;
	}
	
	static public MDocType instance(Class<? extends MRemnantsImpl> type) {
		instance = new MRemnantsDoc(type);
		return instance;
	}

	@Override
	public int getDocTitle() { return string.remains_doc_title;	}
}
