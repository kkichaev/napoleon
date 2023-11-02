package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.DMPImpl;
import com.grsoft.napoleon.R;

public class DMPDoc extends DocType {
	private static final String OBJECT_NAME = "DMP";
	private static DMPDoc instance = null;
	
	public static DMPDoc instance() {
		if (instance == null)
			instance = new DMPDoc();
		
		return instance;
	}
	
	protected DMPDoc() {
		super(OBJECT_NAME,  OBJECT_NAME,  DMPImpl.class);
	}

	@Override
	public int getResurceId() {
		return R.drawable.dmp_doc;
	}
	
	@Override
	public int getDocTitle() {
		return R.string.dmp_doc;
	}
}
