package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.MerchImplEnd;
import com.grsoft.napoleon.R;



public class MerchEndDoc extends DocType {
	static public final String DOC_NAME = "Мерч конец";
	static public final String OBJ_NAME = "MerchEnd";
	private static DocType instance;
	
	protected MerchEndDoc() {
		super(DOC_NAME, OBJ_NAME, MerchImplEnd.class);
	}
	
	static public DocType instance() {
		if( instance == null ) {
			instance = new MerchEndDoc();
		}
		
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.merch;}
}
