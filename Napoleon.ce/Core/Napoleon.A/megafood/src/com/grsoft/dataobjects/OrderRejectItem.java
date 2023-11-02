package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrderRejectItem extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";
	
	@FieldOrder(order = 1)
	public String reason = "";
}
