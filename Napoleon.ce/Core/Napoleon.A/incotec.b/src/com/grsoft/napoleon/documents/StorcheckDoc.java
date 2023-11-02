package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.StorcheckImpl;
import com.grsoft.napoleon.R;

public class StorcheckDoc extends DocType {

	static StorcheckDoc instance = null;
	
	public static StorcheckDoc instance() {
		if(instance == null) {
			instance = new StorcheckDoc();
		}
		
		return instance;
	}

	private StorcheckDoc() {
		super("Сторчек", "Storcheck", StorcheckImpl.class);
	}
	
	@Override public int getResurceId() { return R.drawable.storcheck; }
}
