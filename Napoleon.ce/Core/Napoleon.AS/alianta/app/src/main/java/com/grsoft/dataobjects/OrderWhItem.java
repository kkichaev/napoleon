package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderWhItem extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";

	@Scale(value=Consts.QTY_SCALE)
	@FieldOrder(order = 1)
	public int qty = 0;

	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order = 2)
	public int cost = 0;

	@FieldOrder(order = 3)
	public int pack = 0;

	@FieldOrder(order = 4)
	public int year = 0;
}
