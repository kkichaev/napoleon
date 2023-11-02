package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.util.Consts;

public class OrderImpl2Ex extends OrderImplEx {
	
	@Override
	protected int checkPriceQty(PriceImpl p, int qty, OrderItem item) {
		final int UNFIRE_REST = 24 * Consts.QTY_SCALE;
		int newQty = qty;
		int dataQty = p.getData().qty - UNFIRE_REST;
		int priceQty = dataQty;
		if( item != null ) priceQty += item.qty;
		
		if( priceQty < qty ) {
			if( dataQty < 0 ) newQty = 0;
			else newQty = priceQty;
		}
		
		return newQty;
	}
}
