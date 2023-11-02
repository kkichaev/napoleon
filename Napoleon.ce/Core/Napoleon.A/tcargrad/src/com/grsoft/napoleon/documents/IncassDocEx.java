package com.grsoft.napoleon.documents;

import com.grsoft.network.DocExportListener;

public class IncassDocEx extends IncassDoc {
	static public DocType instance() {
		instance = new IncassDocEx();
		return instance;
	}
	
	@Override
	public DocExportListener getDirtyDocuments() {
		return new DocSendListner(getObjectName(), new EmptyDocList(docClass));
	}
}

