package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DiscountItem extends DataObject {
	
	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order=0)
	public int discount;

	@Scale(value=Consts.QTY_SCALE)
	@FieldOrder(order=1)
	public int qty;

	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order=2)
	public int sum;

	@FieldOrder(order=3)
	public List<DiscountPriceItem> items;
}
