package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrgParam extends DataObject {
	@FieldOrder(order=0)
	public String key;
	
	@FieldOrder(order=1)
	public String value;
}
