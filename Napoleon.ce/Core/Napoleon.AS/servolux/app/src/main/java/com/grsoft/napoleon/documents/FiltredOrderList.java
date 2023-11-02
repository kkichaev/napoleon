package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.HashSet;

import com.grsoft.dataobjects.OrderEx;

public class FiltredOrderList extends DocList {
	public FiltredOrderList(HashSet<String> disabledFirms, DocList docList) {
		document = OrderDoc.instance().create();
		ids = new ArrayList<Long>();
		for(Document<?> doc : docList) {
			OrderEx od = (OrderEx) doc.getData();
			if( disabledFirms.contains(od.firmCode))
				continue;
			
			ids.add(doc.getRowid());
		}
	}
}
