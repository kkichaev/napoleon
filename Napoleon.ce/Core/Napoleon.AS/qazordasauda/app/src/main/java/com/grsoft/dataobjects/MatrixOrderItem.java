package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class MatrixOrderItem extends DataObject {
	@FieldOrder(order=0)
	public String name;
	
	@FieldOrder(order=1)
	public int order;
}
