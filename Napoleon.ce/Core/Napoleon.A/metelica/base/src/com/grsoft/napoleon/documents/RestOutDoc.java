package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.RestOutImpl;
import com.grsoft.napoleon.R;

public class RestOutDoc extends DateDocType {
	private static RestOutDoc instance;
	
	public static DocType instance() {
		if( instance == null )
			instance = new RestOutDoc();
		return instance;
	}
	
	private RestOutDoc() {
		super("Контроль остатков", "RestOut", RestOutImpl.class);
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.rest_out_doc;
	}
}
