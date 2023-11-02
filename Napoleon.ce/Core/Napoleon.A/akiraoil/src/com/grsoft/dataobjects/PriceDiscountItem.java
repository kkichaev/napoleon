package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceDiscountItem extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";
	
	@FieldOrder(order = 1)
	public int costype = 0;

	@FieldOrder(order = 2)
	@Scale(value=Consts.SUM_SCALE)
	public int discount = 0;
}
