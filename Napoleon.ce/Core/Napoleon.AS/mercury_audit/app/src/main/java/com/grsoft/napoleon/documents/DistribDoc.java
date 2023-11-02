package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.DistribImpl;
import com.grsoft.napoleon.R;

public class DistribDoc extends DocType {
	private static final String OBJECT_NAME = "OrgDistrib";
	private static DistribDoc instance = null;
	
	public static DistribDoc instance() {
		if (instance == null)
			instance = new DistribDoc();
		
		return instance;
	}
	
	protected DistribDoc() {
		super(OBJECT_NAME,  OBJECT_NAME,  DistribImpl.class);
	}

	@Override
	public int getResurceId() {
		return R.drawable.distrib_doc;
	}

	@Override
	public int getResurce2Id() {
		return R.drawable.distrib_doc2;
	}

	@Override
	public int getDocTitle() {
		return R.string.distr_doc;
	}
}
