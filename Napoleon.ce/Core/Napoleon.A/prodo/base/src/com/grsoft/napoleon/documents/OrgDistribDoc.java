package com.grsoft.napoleon.documents;


import com.grsoft.dataobjects.impl.OrgDistribImpl;
import com.grsoft.napoleon.R;

public class OrgDistribDoc extends DateDocType {
	static OrgDistribDoc instance;
	public static OrgDistribDoc instance() {
		if( instance == null )
			instance = new OrgDistribDoc();
		return instance;
	}
	
	public OrgDistribDoc() {
		super("Дистр.матрица", "OrgDistribution", OrgDistribImpl.class);
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.distrib_doc;
	}
}
