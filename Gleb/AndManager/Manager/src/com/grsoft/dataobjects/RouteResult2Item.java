package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class RouteResult2Item extends DataObject {
	@FieldOrder(order=0)
	public String name = "";
	@FieldOrder(order=1)
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;
	@Scale(value=Consts.SUM_SCALE)
	public int cost = 0;
}
