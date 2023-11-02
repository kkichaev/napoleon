package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.PKO1cDoc;
import com.grsoft.napoleon.documents.Selector;
import com.grsoft.util.DocFilterOnClickListener;

public class DocListEx extends DocList {
	@Override
	protected DocFilterOnClickListener createDocListFilter() {
		return new DocFilterEx(this, true, false);
	}
	
	@Override protected boolean countSumFromDocuments(boolean useFilter) { return true; }
}

class DocFilterEx extends DocFilterOnClickListener {

	public DocFilterEx(Selector docTypeSelector, boolean createable, boolean showScriptOnly) {
		super(docTypeSelector, createable, showScriptOnly);
	}

	@Override
	protected void initData(boolean creatableFilter) {
		boolean addDoc = data.size() == 0;
		super.initData(creatableFilter);
		if(addDoc)
			data.add(PKO1cDoc.instance());
	}
}
