package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.RejectActImpl;

public class RejectActDoc extends DateDocType {
	static RejectActDoc instance;
	
	public static RejectActDoc instance() {
		if(instance == null) 
			instance = new RejectActDoc();
		return instance;
	}
	
	RejectActDoc() {
		super("Актирование", "RejectAct", RejectActImpl.class);
	}
}
