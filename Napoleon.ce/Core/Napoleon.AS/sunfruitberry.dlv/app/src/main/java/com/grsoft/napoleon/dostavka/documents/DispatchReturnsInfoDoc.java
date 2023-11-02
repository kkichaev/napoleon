package com.grsoft.napoleon.dostavka.documents;

import com.grsoft.dataobjects.impl.DispatchReturnsInfoImpl;
import com.grsoft.napoleon.documents.DocType;

public class DispatchReturnsInfoDoc extends DocType {
	static DispatchReturnsInfoDoc instance;
	
	public static DispatchReturnsInfoDoc instance() {
		if(instance == null)
			instance = new DispatchReturnsInfoDoc();
		return instance;
	}
	
	DispatchReturnsInfoDoc() {
		super("DispatchReturnsInfo","DispatchReturnsInfo",DispatchReturnsInfoImpl.class);
	}
}
