package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceSalesQty extends DataObject implements Comparable<PriceSalesQty> {
	@FieldOrder(order=0)
	public String id = "";

	@FieldOrder(order=1)
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;

	@FieldOrder(order=2)
	public String name = "";

	@Override
	public int compareTo(PriceSalesQty arg0) {
		return id.compareTo(arg0.id);
	}
}
