package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;

public class DocListEx extends DocList {
	@Override
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		return (doc.getDescription(this).toUpperCase().equals("«¿ –€“¿")) ? R.drawable.doc_close :
			super.getDocStatusResource(doc);
	}
	
	@Override
	protected int getDocColor(Document<?> doc) {
		if(DocType.getCurDoc() == OrderDoc.instance())
			return OrderDoc.instance().getViewTextColor(this, doc);
		
		return super.getDocColor(doc);
	}
}
