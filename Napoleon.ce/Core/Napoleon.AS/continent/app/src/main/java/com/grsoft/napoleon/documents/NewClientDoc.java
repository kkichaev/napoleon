package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.NewClientImpl;

public class NewClientDoc extends DocType {
	
	static NewClientDoc instance;
	
	public static NewClientDoc instance() {
		if(instance == null)
			instance = new NewClientDoc();
		return instance;
	}
	
	NewClientDoc() {
		super("Новый клиент", "NewClient", NewClientImpl.class);
	}

}
