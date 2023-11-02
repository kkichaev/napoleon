package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;

public class UnitItem extends DataObject {

	@FieldOrder(order=0)
	@Scale(value=1)
	public int id;
	
	@FieldOrder(order=1)
	public String name;
}
