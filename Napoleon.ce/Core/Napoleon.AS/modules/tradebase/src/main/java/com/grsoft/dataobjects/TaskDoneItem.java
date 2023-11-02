package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;


public class TaskDoneItem extends DataObject {
	@FieldOrder(order=0)
	public String id = ""; 
	@FieldOrder(order=1)
	public String text = "";
	@FieldOrder(order=2)
	public int done = 0;
}
 