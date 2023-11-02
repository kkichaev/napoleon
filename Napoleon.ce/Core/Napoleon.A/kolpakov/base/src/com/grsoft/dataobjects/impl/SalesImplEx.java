package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.SalesItemEx;

public class SalesImplEx extends SalesImpl {
	@Override
	protected void beforeItemWrite(OrderItem item, Price p) {
		((SalesItemEx)item).ido = ((PriceEx)p).ido;
	}

}
