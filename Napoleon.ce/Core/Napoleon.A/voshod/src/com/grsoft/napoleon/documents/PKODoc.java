package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.PKOImpl;
import com.grsoft.napoleon.R;

public class PKODoc extends DocType {
	private static PKODoc instance = null;
	
	private PKODoc() { super("ойн", "PKO", PKOImpl.class); }
	
	static public DocType instance() {
		if( instance == null )
			instance = new PKODoc();
		
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.incass; }
}
