package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.MonitoringDocImpl;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.modules.MonitoringInit;

public class MonitoringDoc extends DateDocType {
	static MonitoringDoc instance;
	
	protected MonitoringDoc() {
		super("Мониторинг", "Monitoring", MonitoringDocImpl.class);
	}
	
	public static DocType instance() {
		if( instance == null ) {
			MonitoringInit.init();
			instance = new MonitoringDoc();
		}
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.monitor_doc; }
}
