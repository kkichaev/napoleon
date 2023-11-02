package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.ExistOutImpl;
import com.grsoft.napoleon.R;

public class ExistDoc extends DateDocType {
	static ExistDoc instance;
	
	public static ExistDoc instance() {
		if(instance == null)
			instance = new ExistDoc();
		return instance;
	}
	
	ExistDoc() {
		super("Наличие", "ExistOut", ExistOutImpl.class);
	}

	@Override public int getResurceId() { return R.drawable.exist_doc; }
}
