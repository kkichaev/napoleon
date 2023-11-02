package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.AliantaOfferImpl;
import com.grsoft.napoleon.R;

public class AliantaOfferDoc extends DocType {
	static AliantaOfferDoc instance = null;
	
	public static AliantaOfferDoc instance() {
		if(instance == null)
			instance = new AliantaOfferDoc();
		return instance;
	}
	
	AliantaOfferDoc() {
		super("AliantaOffer", "AliantaOffer", AliantaOfferImpl.class);
	}

	@Override public int getDocTitle() { return R.string.offer_doc; }

	@Override public int getResurceId() { return R.drawable.offer_doc; }
	@Override public int getResurce2Id() { return R.drawable.offer_doc_2; }
}
