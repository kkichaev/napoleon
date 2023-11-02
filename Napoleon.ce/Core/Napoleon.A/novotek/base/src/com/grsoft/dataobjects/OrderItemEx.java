package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrderItemEx extends OrderItem{
	@FieldOrder(order=4)
	public String unit = "";
	
	@FieldOrder(order=10)
	public int outQty = 0;
}
