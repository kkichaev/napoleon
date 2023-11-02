package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;

public class OrderImplEx extends OrderImpl{
	@Override
	protected void postItemUpdate(OrderItem item) {
		super.postItemUpdate(item);
		
		((OrderItemEx)item).discount = 0;
	}
}
