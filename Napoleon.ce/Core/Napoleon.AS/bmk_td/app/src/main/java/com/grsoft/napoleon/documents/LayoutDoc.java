package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.LayoutImpl;
import com.grsoft.napoleon.R;

public class LayoutDoc extends DocType{
	private static String OBJ_NAME = "Layout";
	private static LayoutDoc instance;
	
	protected LayoutDoc() {
		super(OBJ_NAME, OBJ_NAME, LayoutImpl.class);
	}
	
	static public DocType instance() {
		if( instance == null )
			instance = new LayoutDoc();
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.distrib_doc; }

	@Override
	public int getResurce2Id() {
		return R.drawable.distrib_doc_2;
	}

	@Override public int getDocTitle() { return R.string.layout_doc_title;}
}
