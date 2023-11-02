package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentsAdapter;

public class DocumentsEx extends DocumentsPrint {
	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		return new DocumentsAdapter(this, docType, id, "[created] desc");
	}
}
