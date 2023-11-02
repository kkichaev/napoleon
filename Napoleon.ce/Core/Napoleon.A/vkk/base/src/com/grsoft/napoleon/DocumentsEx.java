package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;

public class DocumentsEx extends Documents {
	@Override
	protected boolean canCreateDoc(DocType docType) {
		
		OrgEx oe = (OrgEx)org.getData();
		if( docType == IncassDoc.instance() )
			return ((oe.flags & OrgEx.INCASS_FLAG) != 0); 

		return super.canCreateDoc(docType);
	}
}
