package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.InvAuditImpl;
import com.grsoft.napoleon.R;


public class InvAuditDoc extends DateDocType {
	static protected InvAuditDoc instance = null;
	
	protected InvAuditDoc() {
		super("InvAudit", "InvAudit", InvAuditImpl.class);
	}
	
	static public DocType instance() {
		if( instance == null )
			instance = new InvAuditDoc();
		return instance;
	}

	@Override
	public int getDocTitle() { return R.string.inv_doc; }
	
	@Override
	public int getResurceId() {	return R.drawable.inv_doc; }

	@Override
	public int getResurce2Id() {
		return R.drawable.inv_doc2;
	}
}
