package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.DiscountMonitoringImpl;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.DateDocType;

public class DiscountMonitoringDoc extends DateDocType {
	static DiscountMonitoringDoc instance;
	
	protected DiscountMonitoringDoc() {
		super("Мониторинг", "Monitoring", DiscountMonitoringImpl.class);
	}
	
	
	public static DocType instance() {
		if( instance == null ) {
			instance = new DiscountMonitoringDoc();
		}
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.monitor_doc; }
}
