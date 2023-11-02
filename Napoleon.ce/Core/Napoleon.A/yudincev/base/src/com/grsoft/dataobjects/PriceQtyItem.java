package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceQtyItem extends DataObject {
	@FieldOrder(order=0)
	public int colorid;
	
	@FieldOrder(order=1)
	public int sizeid;
	
	@FieldOrder(order=2)
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
}
