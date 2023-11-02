package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;

public class OrderImplEx extends OrderImpl {
	@Override
	protected void beforeItemWrite(OrderItem item, Price p) {
		OrderItemEx oie = (OrderItemEx)item;
		if(oie.costCode == null || oie.costCode.length() == 0) {
			OrderEx oe = (OrderEx) data;
			oie.costCode = oe.costCode;
			oie.costIndex = oe.sumType;
		}
	}
	
	@Override
	public void updateItemsCost(int sumType) {
	}
}
