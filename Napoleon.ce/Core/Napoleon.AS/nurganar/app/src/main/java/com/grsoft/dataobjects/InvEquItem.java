package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class InvEquItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	@FieldOrder(order=1)
	public String barcode = "";
	
	@FieldOrder(order=2)
	public String number = "";
	
	@FieldOrder(order=3)
	public String name = "";
	
	@FieldOrder(order=4)
	public int newItem = 0;
	
	@FieldOrder(order=5)
	public int check = 0;
	
	@FieldOrder(order=6)
	public String remark = "";
}
