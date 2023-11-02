package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceWhData extends PriceQtyItem {

	@FieldOrder(order=10)
	@Scale(value=Consts.QTY_SCALE)
	public int res;
}
