package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;

public class OrderItemEx extends OrderItem {

	@FieldOrder(order=USER_FIELDS)
	@FieldVersion(version=1)
	public int whIdx = 0;
}
