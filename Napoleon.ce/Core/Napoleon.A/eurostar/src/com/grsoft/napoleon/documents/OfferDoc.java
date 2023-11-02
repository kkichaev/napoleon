package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.OfferImpl;
import com.grsoft.napoleon.R;


public class OfferDoc extends DocType{
	private static OfferDoc instance;
	
	protected OfferDoc(){
		super("Ком.предложение", "OfferDoc", OfferImpl.class);
	}
	
	public static DocType instance() {
		if( instance == null )
			instance = new OfferDoc();
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.offer_doc; }
}
