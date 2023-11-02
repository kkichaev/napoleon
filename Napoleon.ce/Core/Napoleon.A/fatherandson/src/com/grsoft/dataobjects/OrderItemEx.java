package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrderItemEx extends OrderItem {
	@FieldOrder(order=USER_FIELDS)
	public String unitId;
	
	@FieldOrder(order=USER_FIELDS + 1)
	public String remark;
}
