package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.VandSellDoc;


public class DocListEx extends DocList {
	@Override
	protected DocType getDefaultDocType() { return VandSellDoc.instance(); }
}
