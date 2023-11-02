package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.ProcurationImpl;
import com.grsoft.napoleon.R;


public class ProcurationDoc extends DocType {
	public static final String OBJECT_NAME = "Procuration"; 
	static protected ProcurationDoc instance = null;
	
	protected ProcurationDoc() {
		super(OBJECT_NAME, OBJECT_NAME, ProcurationImpl.class);
	}

	static public DocType instance() {
		if( instance == null )
			instance = new ProcurationDoc();
		return instance;
	}
	
	@Override
	public int getDocTitle() { return R.string.procuration_doc_title; }
	@Override
	public int getResurceId() {	return R.drawable.prctdoc; }
}
