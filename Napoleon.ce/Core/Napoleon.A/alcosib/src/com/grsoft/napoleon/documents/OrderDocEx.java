package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.OrderImplEx;

public class OrderDocEx extends OrderDoc {
	
	protected OrderDocEx() {
		super("Заявки", "Order", OrderImplEx.class);
	}

	static public void initialize() {
		instance = new OrderDocEx();
	}
}
