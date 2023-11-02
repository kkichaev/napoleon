package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;


public class DocList2Ex extends DocListEx {
	@Override
	protected DocListAdapter createListAdapter(DocType docType){
		return new DocListAdapter(this, docType, saveDatePeriod, "created");
	}
}
