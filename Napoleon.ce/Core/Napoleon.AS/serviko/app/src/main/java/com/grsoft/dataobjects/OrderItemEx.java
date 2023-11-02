package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;

public class OrderItemEx extends OrderItem {
	@FieldVersion(version=1)
	@FieldOrder(order = USER_FIELDS)
	public String promoId = "";
}
