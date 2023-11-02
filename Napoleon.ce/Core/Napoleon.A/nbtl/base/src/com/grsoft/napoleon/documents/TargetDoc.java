package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.TargetImpl;

public class TargetDoc extends DocType{
	public static String OBJ_NAME = "Target"; 
	private static TargetDoc instance = null;
	
	protected TargetDoc() {
		super(OBJ_NAME, OBJ_NAME, TargetImpl.class);
	}
	
	public static DocType instance() {
		if(instance == null)
			instance = new TargetDoc();
		
		return instance;
	}

}
