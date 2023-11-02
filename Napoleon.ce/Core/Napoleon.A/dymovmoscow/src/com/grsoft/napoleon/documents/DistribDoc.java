package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.DistribImpl;
import com.grsoft.napoleon.R;

public class DistribDoc extends DocType{
	static DistribDoc instance;
	
	public static DistribDoc instance() {
		if( instance == null )
			instance = new DistribDoc();
	
		return instance;
	}
	
	protected DistribDoc() {
		super("Distrib", "Distrib", DistribImpl.class);
	}

	@Override public int getDocTitle() {	return R.string.distrib; }
	@Override public int getResurceId() { return R.drawable.distr_doc; }
}
