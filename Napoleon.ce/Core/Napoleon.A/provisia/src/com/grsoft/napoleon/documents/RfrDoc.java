package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.RfrOutImpl;
import com.grsoft.napoleon.R;

public class RfrDoc extends DateDocType {
	static RfrDoc instance;
	
	
	RfrDoc() {
		super("Холод.оборудование", "RfrDoc", RfrOutImpl.class);
	}
	
	public static DocType instance() {
		if( instance == null )
			instance = new RfrDoc();
		return instance;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.rfr_doc;
	}
}
