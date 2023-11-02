package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderItemEx extends OrderItem {
	public String idWh = "";

	@Scale(value=Consts.SUM_SCALE)
	public int discount;

	public int dscType;
}
