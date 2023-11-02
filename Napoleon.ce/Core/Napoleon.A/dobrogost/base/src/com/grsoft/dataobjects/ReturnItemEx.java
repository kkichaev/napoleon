package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class ReturnItemEx extends OrderItemEx {
	@FieldOrder(order=10)
	public String cause;
	
	@FieldOrder(order=11)
	public String remark = "";
	
	@FieldOrder(order=12)
	public int inKG = 0;
}
