package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;

public class OrderImplEx extends OrderImpl {
	
	@Override
	public void postInit() {
		OrgImpl oi = new OrgImpl();
		oi.read("id", data.id);
		((OrderEx)data).name = oi.getData().name;
	}
	
	@Override
	protected void beforeItemWrite(OrderItem item, Price p) {
		((OrderItemEx)item).name = p.name;
	}

}
