package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class ReturnItem extends OrderItem {
	@FieldOrder(order=OrderItem.USER_FIELDS)
	public String cause;
}
