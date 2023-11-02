package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class RestOutItem extends DataObject {
	@FieldOrder(order=0)
	public String id;
	
	@FieldOrder(order=1)
	public int plan;

	@FieldOrder(order=2)
	public int qty;

	@FieldOrder(order=3)
	public int order;
}
