package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrderItemEx extends OrderItem {
	
	@FieldOrder(order=OrderItem.USER_FIELDS + 1)
	public int decl = 0;
}
