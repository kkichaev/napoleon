package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceSizeCost extends DataObject {
	@FieldOrder(order=0)
	public String size = "";
	
	@FieldOrder(order=1)
	@Scale(value=Consts.SUM_SCALE)
	public int cost = 0;
}
