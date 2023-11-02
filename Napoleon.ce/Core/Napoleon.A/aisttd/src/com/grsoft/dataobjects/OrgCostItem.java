package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrgCostItem extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";

	@FieldOrder(order = 1)
	public int isItem = 0;
	
	@FieldOrder(order = 2)
	public int costype = 0;
}
