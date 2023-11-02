package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class InvFrgItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	@FieldOrder(order=1)
	public String barcode = "";
	
	@FieldOrder(order=2)
	public String number = "";
	
	@FieldOrder(order=3)
	public String name = "";

	@FieldOrder(order=4)
	public int isnew = 0;

	@FieldOrder(order=5)
	public int exist = 0;
}
