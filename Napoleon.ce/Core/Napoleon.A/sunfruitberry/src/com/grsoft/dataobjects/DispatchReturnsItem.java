package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DispatchReturnsItem extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";
	
	@FieldOrder(order = 1)
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;
	
	@FieldOrder(order = 2)
	public String cause = "";

	@FieldOrder(order = 3)
	@FieldVersion(version=1)
	@Scale(value=Consts.SUM_SCALE)
	public int cost = 0;
}
