package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class ItemsAuditItem extends DataObject {
	@FieldOrder(order=0)
	public String id;

	@FieldOrder(order=1)
	public int repr;

	@FieldOrder(order=1)
	public int pack;

	@FieldOrder(order=1)
	public int block;

	@FieldOrder(order=1)
	public int price;
}
