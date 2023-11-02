package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocType;

public class DocumentsEx extends Documents {
	
	@Override
	protected boolean canCreateDoc(DocType docType) {
		return docType.isCreatable();
	}
	
	@Override
	protected String orgInfo(Org o) {
		String ret = super.orgInfo(o);
		String info = ((OrgEx)o).info;
		if(info.length() > 0) {
			ret += "<br/>" + info;
		}
		return ret;
	}
}
