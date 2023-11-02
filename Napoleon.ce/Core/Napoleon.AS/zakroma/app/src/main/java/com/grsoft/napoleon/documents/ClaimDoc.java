package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.ClaimImpl;
import com.grsoft.napoleon.R;

public class ClaimDoc extends DateDocType {
	static ClaimDoc instance = null;
	
	public static ClaimDoc instance() {
		if(instance == null)
			instance = new ClaimDoc();
		return instance;
	}
	
	ClaimDoc() {
		super("Претензия", "Claim", ClaimImpl.class);
	}

	@Override
	public int getResurce2Id() {
		return R.drawable.claim_doc_2;
	}

	@Override
	public int getResurceId() {
		return R.drawable.claim_doc;
	}
}
