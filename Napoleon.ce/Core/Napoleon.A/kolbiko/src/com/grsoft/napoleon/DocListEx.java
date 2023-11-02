package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class DocListEx extends DocList {
	@Override
	protected int getDocSum(Document<?> doc) {
		
		if( doc instanceof OrderImplBase<?> ) {
			int qty = 0;
			OrderImplBase<?> o = (OrderImplBase<?>)doc;
			for(OrderItem oi : o.getData().items)
				qty += oi.qty;
			
			return (int)((long)(Consts.SUM_SCALE * qty) / Consts.QTY_SCALE);
		}
		return super.getDocSum(doc);
	}
	
	@Override protected void refreshTotalSum(boolean useFilter) { 
		super.refreshTotalSum(true);
	}
}
