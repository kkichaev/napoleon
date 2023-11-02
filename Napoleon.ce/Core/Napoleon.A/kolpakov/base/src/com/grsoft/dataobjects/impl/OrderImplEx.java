package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;

public class OrderImplEx extends OrderImpl {
	@Override
	protected void beforeItemWrite(OrderItem item, Price p) {
		((OrderItemEx)item).ido = ((PriceEx)p).ido;
	}
}
