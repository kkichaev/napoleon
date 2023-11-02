package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.MerchImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;


public class MerchBeginDoc extends DocType{
	static public final String DOC_NAME = "Ìונק םאקאכמ";
	static public final String OBJ_NAME = "MerchBegin";
	private static DocType instance;
	
	protected MerchBeginDoc() {
		super(DOC_NAME, OBJ_NAME, MerchImpl.class);
	}
	
	static public DocType instance() {
		if( instance == null ) {
			instance = new MerchBeginDoc();
		}
		
		return instance;
	}
	
	@Override public DocExportListener getDirtyDocuments() { return null; }
	
	@Override public int getResurceId() { return R.drawable.merch;}
}
