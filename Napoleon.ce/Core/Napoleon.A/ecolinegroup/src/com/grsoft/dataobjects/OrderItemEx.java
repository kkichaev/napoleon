package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;


public class OrderItemEx extends OrderItem {
	@FieldOrder(order=4)
	public int costidx = 0;
	@FieldOrder(order=5)
	public int useact = 1;
	
}
