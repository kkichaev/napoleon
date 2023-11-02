package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class DeliverySklad extends DataObject {
	@FieldOrder(order=0)
	public int skladid;
	
	@FieldOrder(order=1)
	public int flags;
	
	@FieldOrder(order=2)
	public String remark = "";
	
	@FieldOrder(order=3)
	public String numdlvsk = "";
}
