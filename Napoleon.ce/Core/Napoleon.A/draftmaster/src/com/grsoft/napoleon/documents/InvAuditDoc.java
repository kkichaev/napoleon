package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.InvAuditImpl;
import com.grsoft.napoleon.R;


public class InvAuditDoc extends DocType {
	public static final String OBJNAME = "InvAudit";
	private static DocType instance;
	
	static public DocType instance() {
		if( instance == null )
			instance = new InvAuditDoc();
		return instance;
	}
	
	protected InvAuditDoc() {
		super(OBJNAME, OBJNAME, InvAuditImpl.class);
	}
	
	@Override public int getResurceId() { return R.drawable.inv_doc;	}
	
	@Override public int getDocTitle() { return R.string.inv_audit_doc_title;	}

}
