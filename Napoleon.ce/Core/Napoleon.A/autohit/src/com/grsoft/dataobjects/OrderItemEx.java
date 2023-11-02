package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderItemEx extends OrderItem {
	@FieldOrder(order=1000)
	@Scale(value=Consts.SUM_SCALE)
	public int discount;
	@FieldOrder(order=1001)
	public String unit = "";

}
