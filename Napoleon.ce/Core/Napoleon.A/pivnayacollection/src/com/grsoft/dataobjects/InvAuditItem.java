package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class InvAuditItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;
	
	@Scale(value=Consts.QTY_SCALE)
	@FieldOrder(order=2)
	public int fact = 0;
	
	@FieldOrder(order=3)
	public int clear = 0;
	
	@Scale(value=Consts.QTY_SCALE)
	@FieldOrder(order=4)
	public int good = 0;
}
