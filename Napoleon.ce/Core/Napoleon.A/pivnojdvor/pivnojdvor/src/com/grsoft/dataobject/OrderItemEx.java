package com.grsoft.dataobject;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.types.FieldOrder;

public class OrderItemEx extends OrderItem {
	@FieldOrder(order=USER_FIELDS+1)
	public int tag = 0;
}
