package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrderItemEx extends OrderItem{
	@FieldOrder(order=100)
	public String unit = "";
}
