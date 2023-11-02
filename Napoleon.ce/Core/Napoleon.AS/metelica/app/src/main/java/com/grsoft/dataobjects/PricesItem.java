package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PricesItem extends DataObject {
	@FieldOrder(order = 1)
	public String id = "";

	@FieldOrder(order = 2)
	@Scale(value = Consts.SUM_SCALE)
	public int cost = 0;

	@FieldOrder(order = 3)
	@Scale(value= Consts.SUM_SCALE)
	public int minCost = 0;

	@FieldOrder(order = 4)
	@Scale(value= Consts.SUM_SCALE)
	public int mgrCost = 0;
}
