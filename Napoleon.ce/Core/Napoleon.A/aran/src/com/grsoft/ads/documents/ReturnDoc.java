package com.grsoft.ads.documents;

import com.grsoft.ads.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.documents.DocType;

public class ReturnDoc extends DocType {
	static private ReturnDoc instance = null;
	
	protected ReturnDoc() {
		super("Returns", "Returns", ReturnImpl.class);
	}
	
	static public DocType instance() {
		if( instance == null )
			instance = new ReturnDoc();
		return instance;
	}

}
