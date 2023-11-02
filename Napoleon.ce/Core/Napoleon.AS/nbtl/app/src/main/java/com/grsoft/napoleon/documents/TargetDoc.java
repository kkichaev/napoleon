package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.TargetImpl;
import com.grsoft.napoleon.R;

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

	@Override
	public int getResurceId() {
		return R.drawable.target_doc;
	}

	@Override
	public int getResurce2Id() {
		return R.drawable.target_doc_2;
	}

	@Override
	public int getDocTitle() {
		return R.string.target_doc_title;
	}
}
