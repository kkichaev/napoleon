package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.ArchReturnImpl;
import com.grsoft.napoleon.R;


public class ArchReturnDoc extends ReturnDoc {
	static ArchReturnDoc archInstance = null;
	
	static public DocType instance() {
		if( archInstance == null )
			archInstance = new ArchReturnDoc();
		return archInstance;
	}

	ArchReturnDoc() {
		super("Арх.возврат", "ArchReturns", ArchReturnImpl.class);
	}
	
	@Override public int getResurceId() { return R.drawable.arch_incass; }
	
	@Override
	public int getDocTitle() {
		return -1;
	}
	
	public int getResurce2Id() {
		return getResurceId();
	};
}
