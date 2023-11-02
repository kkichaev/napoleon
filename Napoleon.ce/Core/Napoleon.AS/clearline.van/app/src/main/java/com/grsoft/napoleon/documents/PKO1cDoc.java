package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.PKO1cImpl;
import com.grsoft.napoleon.R;

public class PKO1cDoc extends DocType {
	static PKO1cDoc instance = null;
	
	public static DocType instance() {
		if(instance == null)
			instance = new PKO1cDoc();
		
		return instance;
	}

	public PKO1cDoc() {
		super("ойн", "PKO1c", PKO1cImpl.class);
	}

	@Override public int getResurceId() {
		return R.drawable.incass_doc;
	}

	@Override public int getResurce2Id() {
		return R.drawable.incass_doc_2;
	}
}
