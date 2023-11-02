package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;

public class DocumentsEx extends Documents {
	@Override
	protected String getOrder(DocType docType) {
		if(docType == DebtDoc.instance())
			return "dlv_pos";
		return super.getOrder(docType);
	}
}
