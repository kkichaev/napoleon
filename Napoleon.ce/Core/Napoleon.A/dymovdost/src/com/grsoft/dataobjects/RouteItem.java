package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.util.ExtrasConst;

public class RouteItem extends DataObject{
	@FieldOrder(order=0)
	public String id = "";
	@FieldOrder(order=1)
	public String text = "";
	@FieldOrder(order=2)
	public String client = "";
	@FieldOrder(order=3)
	public String address = "";
	@FieldOrder(order=4)
	public long responce = ExtrasConst.INVALID_ROWID;
}
