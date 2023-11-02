package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderItemEx extends OrderItem {
	@FieldOrder(order = USER_FIELDS)
	@Scale(value = Consts.SUM_SCALE)
	public int discount = 0;

	@FieldOrder(order = USER_FIELDS + 1)
	@Scale(value = Consts.SUM_SCALE)
	public int costWODsc = 0;

	@FieldOrder(order = USER_FIELDS + 2)
	public int manualCost = 0;
}
