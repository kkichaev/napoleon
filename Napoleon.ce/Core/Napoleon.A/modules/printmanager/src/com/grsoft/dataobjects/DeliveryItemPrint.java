package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DeliveryItemPrint extends DeliveryItem {
	@FieldOrder(order=100)
	@Scale(value=Consts.SUM_SCALE)
	public int taxSum;

}
