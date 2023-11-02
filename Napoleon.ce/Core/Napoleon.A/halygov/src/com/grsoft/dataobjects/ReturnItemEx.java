package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class ReturnItemEx extends OrderItem {
	@FieldOrder(order=OrderItem.USER_FIELDS)
	public String cause;
	@FieldOrder(order=OrderItem.USER_FIELDS+1)
	public String dlvNumber;
}
