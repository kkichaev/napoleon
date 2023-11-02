package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class SalesItemEx extends SalesItem {
	@FieldOrder(order=OrderItem.USER_FIELDS)
	public String ido;
}
