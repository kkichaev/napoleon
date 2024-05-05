package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class OrderItemEx extends OrderItem {
	@FieldOrder(order=USER_FIELDS)
	public int idx;

	@FieldOrder(order=USER_FIELDS + 1)
	public String action = "";

	@FieldOrder(order=USER_FIELDS + 2)
	public int bonus = 0;

	@FieldOrder(order=USER_FIELDS + 3)
	@Scale(value = Consts.SUM_SCALE)
	public int costWOD = 0;

}
