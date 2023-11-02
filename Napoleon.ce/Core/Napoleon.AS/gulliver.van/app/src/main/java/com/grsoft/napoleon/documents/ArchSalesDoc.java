package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.ArchSalesImpl;
import com.grsoft.napoleon.R;


public class ArchSalesDoc extends SalesDoc {
static ArchSalesDoc archInstance = null;
	
	static public DocType instance() {
		if( archInstance == null )
			archInstance = new ArchSalesDoc();
		return archInstance;
	}

	ArchSalesDoc() {
		super("Арх.продажа", "ArchSales", ArchSalesImpl.class);
	}
	
	@Override public int getResurceId() { return R.drawable.arch_incass; }
	@Override public int getResurce2Id() { return R.drawable.arch_incass_2; }

	@Override
	public int getDocTitle() { return -1; }
}
