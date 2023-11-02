package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.RfrgAuditImpl;
import com.grsoft.napoleon.R;

public class RfrgAuditDoc extends DateDocType {
	static RfrgAuditDoc instance;
	
	public RfrgAuditDoc() {
		super("Аудит холодильников", "RdrgAudit", RfrgAuditImpl.class);
	}
	
	public static DocType instance() {
		if( instance == null )
			instance = new RfrgAuditDoc();
		return instance;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.frozen_doc;
	}
}
