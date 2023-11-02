package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnChekBackDoc;

public class DocumentsEx extends Documents {
	@Override
	protected boolean canCreateDoc(DocType docType) {
		if(docType == ReturnChekBackDoc.instance())
			return false;
		return super.canCreateDoc(docType);
	}
}
