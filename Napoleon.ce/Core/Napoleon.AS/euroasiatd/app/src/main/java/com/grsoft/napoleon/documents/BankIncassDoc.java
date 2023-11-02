package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.BankIncassImpl;
import com.grsoft.napoleon.R;

public class BankIncassDoc extends DocType {
	static BankIncassDoc instance = null;
	
	BankIncassDoc() {
		super("Банкомат", "BankIncass", BankIncassImpl.class);
	}
	
	public static DocType instance() {
		if(instance == null)
			instance = new BankIncassDoc();
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.bank_incass; }
}
