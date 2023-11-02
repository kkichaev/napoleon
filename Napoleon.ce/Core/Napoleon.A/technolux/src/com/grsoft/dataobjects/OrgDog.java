package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrgDog extends DataObject {
	@FieldOrder(order = 0)
	public String id;

	@FieldOrder(order = 1)
	public String name;
	
	@FieldOrder(order = 2)
	public String prcType = "";

	@FieldOrder(order = 3)
	public String firm = "";
	
	@Override
	public String toString() { return name;	}
}
