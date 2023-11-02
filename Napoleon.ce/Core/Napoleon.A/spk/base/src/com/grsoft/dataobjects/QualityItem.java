package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class QualityItem extends DataObject {
	@FieldOrder(order=0)
	public String name = "";
	
	@FieldOrder(order=1)
	public String id = "";
	
	@FieldOrder(order=2)
	public int type;
}
