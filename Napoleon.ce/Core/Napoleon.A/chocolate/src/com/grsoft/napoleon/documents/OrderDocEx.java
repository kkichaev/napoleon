package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.OrderImplEx;

public class OrderDocEx extends OrderDoc {
	private OrderDocEx() {
		super("Заявки", "Order", OrderImplEx.class);
	}
	
	static public void initialize() {
		instance = new OrderDocEx();
	}
}
