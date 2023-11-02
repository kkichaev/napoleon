package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.CreatableDocument;

public class DocListEx extends DocList {
	private DocListHelper dlh = new DocListHelper();
	
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		return dlh.getDocStatusResource(doc);
	}
}
