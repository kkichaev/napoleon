package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.PaysImpl;
import com.grsoft.napoleon.R;

public class PaysDoc extends DocType {
	static PaysDoc instance = null;
	
	private PaysDoc() {
		super("Оплаты", PaysImpl.class);
	}
	
	public static DocType instance() {
		if( instance == null )
			instance = new PaysDoc();
		
		return instance;
	}
	
	@Override
	public int getResurceId() { return R.drawable.pays_doc; }
}
