package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.DistribImpl;
import com.grsoft.napoleon.R;

public class DistribDoc extends DateDocType{
	private static final String DOC_NAME = "Дистрибуция";
	private static final String OBJ_NAME = "Distrib";
	private static DistribDoc instance = null;
	
	protected DistribDoc() {
		super(DOC_NAME, OBJ_NAME, DistribImpl.class);
	}

	public static DocTypeBase instance() {
		if(instance == null)
			instance = new DistribDoc();
			
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.distrib_doc; }

	@Override
	public int getResurce2Id() {
		return R.drawable.distrib_doc_2;
	}
}
