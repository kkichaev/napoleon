package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.script.documents.ScriptDoc;

public class DocumentsEx extends Documents {
	@Override
	protected boolean isOrgBlocked(Org o, DocType dt) {
		return ((OrgEx)org.getData()).stopMsg.length() > 0 && (dt == ReturnDoc.instance() || dt == OrderDoc.instance() || dt == ScriptDoc.instance());
	}
	
	@Override
	protected String getStopMessage() {
		return ((OrgEx)org.getData()).stopMsg;
	}
}
