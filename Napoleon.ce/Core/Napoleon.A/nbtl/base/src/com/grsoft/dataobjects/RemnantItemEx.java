package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class RemnantItemEx extends RemnantItem {
	
	@FieldOrder(order=10)
	@Scale(value=Consts.QTY_SCALE)
	public int qtyFact = 0;

	@FieldOrder(order=11)
	@Scale(value=Consts.QTY_SCALE)
	public int qtyInput = 0;

	@FieldOrder(order=12)
	@Scale(value=Consts.QTY_SCALE)
	public int qtyUnload = 0;
	
	@FieldOrder(order=12)
	@Scale(value=Consts.QTY_SCALE)
	public int qtyBrak = 0;
}
