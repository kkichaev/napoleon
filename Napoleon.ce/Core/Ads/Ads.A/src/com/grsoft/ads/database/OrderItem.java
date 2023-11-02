package com.grsoft.ads.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;

public class OrderItem extends DataObject {
	@FieldOrder(order=0)
	public String priceid = "";
	
	@FieldOrder(order=1)
	@Scale(value=1000)
	public int qty;
}
