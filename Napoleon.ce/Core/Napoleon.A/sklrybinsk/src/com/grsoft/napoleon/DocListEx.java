package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.CreatableDocument;

public class DocListEx extends DocList {
	@Override
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		return (doc.getDescription(this).toUpperCase().equals("«¿ –€“¿")) ? R.drawable.doc_close :
			super.getDocStatusResource(doc);
	}
}
