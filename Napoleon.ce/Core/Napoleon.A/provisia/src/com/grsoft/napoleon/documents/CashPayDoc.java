package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.CashPayImpl;
import com.grsoft.napoleon.R;

public class CashPayDoc extends DocType {
	static CashPayDoc instance;
	
	CashPayDoc() {
		super("Кассовый отчет", "DocPay", CashPayImpl.class);
	}
	
	public static DocType instance() {
		if(instance == null)
			instance = new CashPayDoc();
		
		return instance;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.cash_doc;
	}
}