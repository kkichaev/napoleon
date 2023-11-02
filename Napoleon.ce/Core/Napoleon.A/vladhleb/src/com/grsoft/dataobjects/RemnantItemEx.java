package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class RemnantItemEx extends RemnantItem {
	@FieldOrder(order=2)
	@Scale(value=Consts.SUM_SCALE)
	public int cost = 0;
}
