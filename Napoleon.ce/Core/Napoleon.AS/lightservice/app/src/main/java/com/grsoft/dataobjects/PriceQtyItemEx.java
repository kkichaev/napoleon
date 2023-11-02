package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceQtyItemEx extends PriceQtyItem {
	@FieldOrder(order=100)
	@Scale(value=Consts.QTY_SCALE)
	public int freeQty = 0;

	@FieldOrder(order=101)
	@Scale(value=Consts.QTY_SCALE)
	public int rezervQty = 0;
}
