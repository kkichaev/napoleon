package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.OrderImplEx;

public class OrderDocEx extends OrderDoc {
	public static void init() { instance = new OrderDocEx(); }
	
	private OrderDocEx() {
		super("Заявки", "Order", OrderImplEx.class);
	}
}
