package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class InvAuditItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	public String name = "";
	
	@FieldOrder(order=2)
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;
	
	@Scale(value=Consts.QTY_SCALE)
	@FieldOrder(order=3)
	public int fact = 0;
	
	@FieldOrder(order=4)
	public int clear = 0;
	
	@Scale(value=Consts.QTY_SCALE)
	@FieldOrder(order=5)
	public int good = 0;
}
