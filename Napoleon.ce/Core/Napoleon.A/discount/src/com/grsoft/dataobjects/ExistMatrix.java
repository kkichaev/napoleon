package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class ExistMatrix extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";
	
	@FieldOrder(order = 1)
	public int priz = 0;
	
	@FieldOrder(order = 2)
	public int sred = 0;
}
