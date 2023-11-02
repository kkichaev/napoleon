package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Org;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;


public class DocListEx extends DocList {
	StringBuilder sb = new StringBuilder();
	@Override
	protected String getDocText(Org o, Document<?> doc) {
		String result = super.getDocText(o, doc);; 
		if(doc.getClass() == OrderDoc.instance().getDocClass()){
			sb.setLength(0);
			sb.append(result);
			sb.append("<br>").append(((Order)doc.getData()).remark);
			result = sb.toString();
		}
		
		return result;
		
	}
}
