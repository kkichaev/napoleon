package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;


public class DocListEx extends DocList {
	@Override
	protected DocListAdapter createListAdapter(DocType docType) {
		return new DocListAdapter(this, docType, saveDatePeriod, "created DESC");
	}
}
