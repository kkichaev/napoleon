package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.CommonAuditImpl;
import com.grsoft.napoleon.R;

public class CommonAuditDoc extends DateDocType {
	static CommonAuditDoc instance = null;
	
	public CommonAuditDoc() {
		super("Общий аудит", "CommonAudit", CommonAuditImpl.class);
	}
	
	public static DocType instance() {
		if(instance == null)
			instance = new CommonAuditDoc();
		return instance;
	}
	
	@Override public int getDocTytle() { return R.string.common_audit; }
	@Override
	public int getResurceId() {	return R.drawable.common; }
}
