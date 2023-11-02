package com.grsoft.dataobjects;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;

public class Card extends DataObject {
	@FieldOrder(order=0)
	public String id;
	
	@FieldOrder(order=1)
	public String name;
	
	@FieldOrder(order=2)
	public String costype;
}
