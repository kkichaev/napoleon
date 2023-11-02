package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.CellsAuditImpl;
import com.grsoft.napoleon.R;

public class CellsAuditDoc extends DateDocType {
	static CellsAuditDoc instance;
	
	public static DocType instance() {
		if(instance == null)
			instance = new CellsAuditDoc();
		return instance;
	}
	
	CellsAuditDoc() {
		super("Ревизия", "Audit", CellsAuditImpl.class);
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.audit_doc;
	}
}
