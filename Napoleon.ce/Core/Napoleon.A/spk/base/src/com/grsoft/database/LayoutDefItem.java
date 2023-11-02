package com.grsoft.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;


public class LayoutDefItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	@FieldOrder(order=1)
	public String name = "";
}
