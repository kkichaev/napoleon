package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;

public class DocumentsEx extends Documents {
	@Override
	protected boolean canCreateDoc(DocType docType) {
		boolean result = super.canCreateDoc(docType);
		
		if(result && MainEx.hardMode)
			result = OrgHelper.isEnabled(org.getData().id);
		
		return result;
	}
}
