package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class CommonAuditItem extends DataObject {
	@FieldOrder(order=0)
	public String id;
	
	@FieldOrder(order=1)
	public int presents;
	
	@FieldOrder(order=2)
	public String stock;
	
	@FieldOrder(order=3)
	public String price;
	
	@FieldOrder(order=4)
	public String merch;
}
