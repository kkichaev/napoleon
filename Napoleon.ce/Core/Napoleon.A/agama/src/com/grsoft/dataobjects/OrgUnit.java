package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrgUnit extends DataObject {
	@FieldOrder(order=0)
	public int id;

	@FieldOrder(order=1)
	public String name;
}
