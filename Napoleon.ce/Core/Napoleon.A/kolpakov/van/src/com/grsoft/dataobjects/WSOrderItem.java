package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class WSOrderItem extends OrderItem {
	@FieldOrder(order=OrderItem.USER_FIELDS)
	public String ido;
}
