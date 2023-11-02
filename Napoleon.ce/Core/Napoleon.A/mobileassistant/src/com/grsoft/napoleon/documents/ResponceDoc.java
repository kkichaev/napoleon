package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.ResponceImpl;


public class ResponceDoc extends DocType {
	private static final String OBJ_NAME = "NapoleonTaskResponse";
	private static ResponceDoc instance = null;
	
	protected ResponceDoc() {
		super(OBJ_NAME, OBJ_NAME, ResponceImpl.class);
	}
	
	
	static public ResponceDoc instance(){
		if(instance == null)
			instance = new ResponceDoc();
		
		return instance;
	}
}
