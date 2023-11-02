package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.ClientCardDoc;
import com.grsoft.napoleon.documents.DocType;

public class DocumentsEx extends Documents {
	protected String orgInfo(Org o) {
		String ret = super.orgInfo(o);
		
		String info = ((OrgEx)o).getInfo();
		if(info.length() > 0)
			ret += "<br>" + info;
		
		return ret; 
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == ClientCardDoc.instance()) {
			ClientCardReport.open(this, org.getData().id);
			finish();
		}
		
		super.adjustViewForDocType(docType);
	}
}
