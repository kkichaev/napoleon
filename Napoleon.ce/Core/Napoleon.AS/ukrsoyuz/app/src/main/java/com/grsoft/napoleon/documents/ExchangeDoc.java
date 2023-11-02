package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.ExchangeImpl;
import com.grsoft.napoleon.R;

public class ExchangeDoc extends DateDocType {
	static public final String DOC_NAME = "Ξαμεν";
	static public final String OBJ_NAME = "ExchDoc";
	static private ExchangeDoc instance = null;
	
	protected ExchangeDoc() { super(DOC_NAME, OBJ_NAME, ExchangeImpl.class); }

	static public DocType instance() {
		if( instance == null )
			instance = new ExchangeDoc();
		return instance;
	}

	@Override
	public int getResurceId() {
		return R.drawable.sync_doc;
	}

	@Override
	public int getResurce2Id() {
		return R.drawable.sync_doc_2;
	}
}
