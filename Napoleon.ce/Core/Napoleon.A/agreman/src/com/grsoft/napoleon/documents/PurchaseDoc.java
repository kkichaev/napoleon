package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.PurchaseImpl;
import com.grsoft.napoleon.R;

public class PurchaseDoc extends DocType{
	static public final String DOC_NAME = "Purchase";
	static protected PurchaseDoc instance = null;
	
	static public PurchaseDoc instance() {
		if( instance == null )
			instance = new PurchaseDoc();
		
		return instance;
	}
	
	protected PurchaseDoc() {
		super(DOC_NAME, DOC_NAME, PurchaseImpl.class);
	}
	
	@Override
	public int getDocTitle() {
		return R.string.purchase_title;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.purchase;
	}
}
