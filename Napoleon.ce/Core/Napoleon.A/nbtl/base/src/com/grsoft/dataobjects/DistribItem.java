package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class DistribItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	public int exist = 0;
}
