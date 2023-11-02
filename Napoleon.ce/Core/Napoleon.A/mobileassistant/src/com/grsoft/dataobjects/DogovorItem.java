package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class DogovorItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	
	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order=1)
	public int cost;

	@Scale(value=Consts.QTY_SCALE)
	@FieldOrder(order=2)
	public int qtyInPack;
}
