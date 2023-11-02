package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.GwinnerAgentTaskDoc;

public class DocChild extends Documents {
	@Override protected int getContentViewID() { return R.layout.doc_child; }

	@Override
	protected String getOrder(DocType docType) {
		if(docType == GwinnerAgentTaskDoc.instance())
			return "isComplete asc, date desc, created desc";
		return super.getOrder(docType);
	}

}
