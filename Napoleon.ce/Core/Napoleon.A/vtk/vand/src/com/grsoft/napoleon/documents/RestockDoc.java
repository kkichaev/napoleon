package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.RestockImpl;

public class RestockDoc extends DocType {
	static RestockDoc instance;
	
	public static DocType instance() {
		if( instance == null )
			instance = new RestockDoc();
		return instance;
	}
	
	RestockDoc() {
		super("Заявка на борт", "Restock", RestockImpl.class);
	}
}
