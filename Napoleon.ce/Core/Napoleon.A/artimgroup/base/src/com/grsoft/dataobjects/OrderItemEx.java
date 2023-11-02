package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderItemEx extends OrderItem {
	@FieldOrder(order=100)
	public String discid = "";
	
	@FieldOrder(order=101)
	@Scale(value=Consts.SUM_SCALE)
	public int discount;
}
