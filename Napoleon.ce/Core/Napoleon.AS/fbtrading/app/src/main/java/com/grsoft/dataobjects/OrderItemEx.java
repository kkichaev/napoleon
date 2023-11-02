package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderItemEx extends OrderItem {
	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order=USER_FIELDS)
	public int discount = 0;
	
	@FieldOrder(order=USER_FIELDS + 1)
	public int priceCost = 0;
	
	@FieldVersion(version=1)
	@FieldOrder(order=USER_FIELDS + 2)
	public int skladIndex = 0;

	@FieldOrder(order=USER_FIELDS + 3)
	@FieldVersion(version=1)
	public String skladId = "";
}
