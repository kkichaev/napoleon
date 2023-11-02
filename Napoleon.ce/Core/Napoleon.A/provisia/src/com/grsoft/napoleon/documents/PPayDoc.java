package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.PPayImpl;
import com.grsoft.napoleon.R;

public class PPayDoc extends DocType {

	static PPayDoc instance; 
	
	PPayDoc() {
		super("Обещ.платеж", "PPay", PPayImpl.class);
	}
	
	public static DocType instance() {
		if(instance == null)
			instance = new PPayDoc();
		
		return instance;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.pp_doc;
	}
}
