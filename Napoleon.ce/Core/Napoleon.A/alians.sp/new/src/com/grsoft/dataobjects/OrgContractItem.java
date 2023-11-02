package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrgContractItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	public String name = "";
	
	@Override
	public String toString() {
		return name;
	}
}
