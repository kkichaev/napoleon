package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.CreatableDocument;

public class OrderImplEx extends OrderImpl {
	@Override
	protected void beforeItemWrite(OrderItem item, Price p) {
		OrderItemEx oe = (OrderItemEx)item;
		if(oe.costWD == 0)
			oe.costWD = oe.cost;
	}
	
	@Override
	public boolean isEditable() {
		return ((OrderEx)data).action.length() > 0 || super.isEditable();
	}
	
	@Override
	protected void postCopyProcess(CreatableDocument<Order> copy) {
		super.postCopyProcess(copy);
		((OrderEx)copy.getData()).removeAction();
	}
}
