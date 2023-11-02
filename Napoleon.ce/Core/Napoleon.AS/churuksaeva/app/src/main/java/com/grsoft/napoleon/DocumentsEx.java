package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;

public class DocumentsEx extends DocumentsPrint {
	@Override
	protected boolean hideMakePko() {
		return DebtDoc.instance() != DocType.getCurDoc() && super.hideMakePko();
	}
}
