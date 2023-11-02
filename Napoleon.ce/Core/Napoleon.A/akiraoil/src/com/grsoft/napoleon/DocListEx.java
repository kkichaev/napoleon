package com.grsoft.napoleon;

import com.grsoft.dataobjects.Agents;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.RemnantsDoc;

public class DocListEx extends DocList {
	@Override
	protected DocType getDefaultDocType() {
		if(Agents.isDealer()) 
			return RemnantsDoc.instance();
		return super.getDefaultDocType();
	}	
}
