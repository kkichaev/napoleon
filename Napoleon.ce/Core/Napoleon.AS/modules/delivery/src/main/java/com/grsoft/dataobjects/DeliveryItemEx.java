package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DeliveryItemEx extends DeliveryItem {
	@FieldOrder(order=100)
	public int pos = 0;
	
	@FieldOrder(order=101)
	@Scale(value=Consts.SUM_SCALE)
	public int cost = 0;
}
