package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.FacingImpl;
import com.grsoft.napoleon.R;


public class FacingDoc extends DocType {
	static protected FacingDoc instance = null;
	
	protected FacingDoc() {
		super("Facing", "Facing", FacingImpl.class);
	}

	static public DocType instance() {
		if( instance == null )
			instance = new FacingDoc();
		
		return instance;
	}
	
	@Override
	public int getDocTitle() { 	return R.string.facing_doc_title; }
	
	@Override
	public int getResurceId() { return R.drawable.monitor_doc; }

	@Override
	public int getResurce2Id() {
		return R.drawable.monitor_doc_2;
	}

}
