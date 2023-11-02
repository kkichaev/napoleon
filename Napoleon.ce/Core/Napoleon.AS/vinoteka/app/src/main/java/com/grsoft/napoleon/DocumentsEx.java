package com.grsoft.napoleon;

import com.grsoft.dataobjects.MonOrg;
import com.grsoft.dataobjects.Org;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.PriceOrgMonDoc;

public class DocumentsEx extends Documents {
	
	@Override
	protected String orgInfo(Org o) {
		String ret = super.orgInfo(o); 
		if( MonOrg.contains(org.getData()) )
			ret += "<br/><b>Проведите мониторинг</b>";
		return ret;
	}
	
	@Override
	protected boolean canCreateDoc(DocType docType) {
		if( docType == PriceOrgMonDoc.instance())
			return MonOrg.contains(org.getData());
		
		return super.canCreateDoc(docType);
	}
}
