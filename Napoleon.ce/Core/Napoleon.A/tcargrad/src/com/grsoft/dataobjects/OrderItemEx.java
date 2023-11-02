package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderItemEx extends OrderItem {
	@FieldOrder(order=10)
	public String taxType;
	
	@FieldOrder(order=11)
	@Scale(value=Consts.SUM_SCALE)
	public int discount;
		
	public OrderItemEx() {}
	
	public OrderItemEx(OrderItemEx src) {
		id = src.id;
		flags = src.flags;
		qty = src.qty;
		cost = src.cost;
		
		taxType = src.taxType;
	}
}
