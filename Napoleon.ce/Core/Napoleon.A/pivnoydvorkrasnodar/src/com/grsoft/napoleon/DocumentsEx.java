package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;

public class DocumentsEx extends Documents {
	@Override
	protected String orgInfo(Org o) {
		String text = super.orgInfo(o);
		String info = ((OrgEx)o).info;
		if(info.length() > 0) {
			text += "<br/>" + info;
		}
		return text;
	}
	
	@Override
	protected boolean isOrgBlocked(Org o, DocType dt) {
		return dt == OrderDoc.instance() && org.getData().isStopList();
	}
}
