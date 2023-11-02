package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocListEx extends DocList {
	protected String docSumText(Document<?> doc) {
		StringBuilder sb = new StringBuilder();
		sb.append(Util.IntToScaleWStr(getDocSum(doc), Consts.SUM_SCALE, 2, false));
		
		if (DocType.getCurDoc() == OrderDoc.instance()) {
			sb.append("<br>");
			sb.append(Util.IntToScaleWStr(((OrderImpl)doc).weight(), Consts.WEIGHT_SCALE, 3, false));
		}
		
		return sb.toString();
	}
}
