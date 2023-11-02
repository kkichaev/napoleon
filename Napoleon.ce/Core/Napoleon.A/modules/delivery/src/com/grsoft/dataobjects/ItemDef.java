package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class ItemDef extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	@FieldOrder(order=1)
	public String number = "";
	@FieldOrder(order=2)
	public int pos = 0;
	@FieldOrder(order=3)
	public String remark = "";
	@FieldOrder(order=4)
	public String type = "";
	@FieldOrder(order=5)
	public String title = "";
}
