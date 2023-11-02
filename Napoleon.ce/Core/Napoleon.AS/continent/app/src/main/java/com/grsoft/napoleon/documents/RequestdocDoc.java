package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.RequestdocImpl;

public class RequestdocDoc extends DocType {

	static RequestdocDoc instance;

	public static RequestdocDoc instance() {
		if (instance == null)
			instance = new RequestdocDoc();
		return instance;
	}

	protected RequestdocDoc() {
		super("RequestDoc", "RequestDoc", RequestdocImpl.class);
	}
}
