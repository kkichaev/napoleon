package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class TareItem extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";

	@FieldOrder(order = 1)
	@Scale(value = Consts.QTY_SCALE)
	public int qty = 0;
	
	@FieldOrder(order = 0)
	@Scale(value = Consts.QTY_SCALE)
	public int fact = 0;
}
