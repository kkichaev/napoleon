package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;

public class MSalesImpl extends MOrderImplBase<Sales> {

	@Override
	public long sum() {
		long sm = 0;
		if( data.items != null )
			for(OrderItem si : data.items) {
				sm += ((SalesItem)si).sum;
		}
		return sm;
	}
}
