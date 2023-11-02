package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderItemEx extends OrderItem {
	@Scale(value = Consts.SUM_SCALE)
	@FieldOrder(order = USER_FIELDS)
	public int costWO = 0;
}
