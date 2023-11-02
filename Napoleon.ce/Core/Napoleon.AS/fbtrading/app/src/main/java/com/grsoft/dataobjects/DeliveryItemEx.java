package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DeliveryItemEx extends DeliveryItem {
	
	@FieldOrder(order=100)
	@Scale(value=Consts.SUM_SCALE)
	public long cost = 0;
	
	@FieldOrder(order=101)
	@Scale(value=Consts.SUM_SCALE)
	public long discount = 0;
}
