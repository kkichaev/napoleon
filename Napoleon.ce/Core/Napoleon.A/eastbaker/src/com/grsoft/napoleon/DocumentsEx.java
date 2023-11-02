package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;

public class DocumentsEx extends Documents {
	@Override
	protected void refreshTotalSum() {
		if(DocType.getCurDoc() == OrderDoc.instance()) {
			int qty = 0;
			int count = 0;
			for( int i=0; i<adapter.getCount(); i++ ) {
				Document<?> d = (Document<?>) adapter.getItem(i);
				qty += d.qty();
				count++;
			}
			updateTotalSum((long)count * Consts.SUM_SCALE, 0, qty);			
		} else
			super.refreshTotalSum();
	}
}
