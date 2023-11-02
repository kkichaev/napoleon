package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.PriceOrgMonImpl;
import com.grsoft.napoleon.R;

public class PriceOrgMonDoc extends DateDocType {
	public static PriceOrgMonDoc instance = null;
	
	public static PriceOrgMonDoc instance() {
		if( instance == null )
			instance = new PriceOrgMonDoc();
		return instance;
	}
	
	PriceOrgMonDoc() {
		super("Мониторинг", "PriceMonOrgDoc", PriceOrgMonImpl.class);
	}
	
	@Override public int getDocTitle() { return R.string.pricemondoc; }
	
	@Override public int getResurceId() { return R.drawable.prc_mon_doc; }
}
