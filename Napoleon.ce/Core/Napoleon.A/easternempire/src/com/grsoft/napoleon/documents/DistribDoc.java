package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.DistribImpl;
import com.grsoft.napoleon.R;

public class DistribDoc extends DateDocType {
	static DistribDoc instance = null;
	
	public static DocType instance() {
		if( instance == null )
			instance = new DistribDoc();
		return instance;
	}
	
	DistribDoc() {
		super("Дистрибуция", "DistrDoc", DistribImpl.class);
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.distr_doc;
	}

}
