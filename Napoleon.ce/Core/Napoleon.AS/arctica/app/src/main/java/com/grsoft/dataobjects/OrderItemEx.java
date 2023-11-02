package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;

public class OrderItemEx extends OrderItem {
	@FieldOrder(order=USER_FIELDS)
	@FieldVersion(version=1)
	public int sumType = 0;
	
	@FieldOrder(order=USER_FIELDS + 1)
	@FieldVersion(version=2)
	public String unit = "";
}
