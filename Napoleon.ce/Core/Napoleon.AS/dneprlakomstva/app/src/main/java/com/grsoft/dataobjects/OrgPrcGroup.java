package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrgPrcGroup extends DataObject {
	@FieldOrder(order=0)
	public String group = "";

	@FieldOrder(order=1)
	public int isPriceGroup;

	@FieldOrder(order=2)
	public int costType;
}
