package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.ApplyWSOrderImpl;

public class ApplyWSOrderDoc extends DocType {

	static ApplyWSOrderDoc instance;

	public static ApplyWSOrderDoc instance() {
		if (instance == null)
			instance = new ApplyWSOrderDoc();
		return instance;
	}

	protected ApplyWSOrderDoc() {
		super("ApplyOrderCharge", "ApplyOrderCharge", ApplyWSOrderImpl.class);
	}
}
