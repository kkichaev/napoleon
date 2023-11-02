package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderItemEx extends OrderItem {
	@FieldOrder(order=4)
	public String unit = "";
	
	@FieldOrder(order=5)
	@Scale(value=Consts.SUM_SCALE)
	public int prcDD;
	
	@FieldOrder(order=6)
	@Scale(value=Consts.SUM_SCALE)
	public int sumDD;
	
	@FieldOrder(order=7)
	@Scale(value=Consts.SUM_SCALE)
	public int discount;
}
