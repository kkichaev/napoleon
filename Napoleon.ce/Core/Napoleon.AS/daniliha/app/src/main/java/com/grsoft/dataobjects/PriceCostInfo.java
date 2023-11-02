package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceCostInfo extends DataObject {
	@FieldOrder(order = 0)
	@Scale(value = Consts.SUM_SCALE)
	public int cost = 0;
	
	@FieldOrder(order = 1)
	public Date start = new Date();

	@FieldOrder(order = 2)
	public Date finish = new Date();
}
