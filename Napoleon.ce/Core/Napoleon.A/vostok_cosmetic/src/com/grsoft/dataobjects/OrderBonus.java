package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderBonus extends DataObject {
	@FieldOrder(order=0)
	public String id;
	
	@Scale(Consts.QTY_SCALE)
	@FieldOrder(order=1)
	public int qty;
}
