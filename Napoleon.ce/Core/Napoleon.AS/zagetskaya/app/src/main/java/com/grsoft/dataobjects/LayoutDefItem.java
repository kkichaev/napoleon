package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;


public class LayoutDefItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	@FieldOrder(order=1)
	public String name = "";
	@FieldOrder(order = 2)
	public int pos = 0;
}
