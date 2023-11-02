package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class VisitItemEx extends VisitItem {
	@FieldOrder(order = 10)
	public String itemId = "";
	
	@FieldOrder(order = 11)
	public String dmpId = "";
	
	@FieldOrder(order = 12)
	public String key = "";
}
