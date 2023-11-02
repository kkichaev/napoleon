package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class ReturnCommitItem extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";

	@FieldOrder(order = 1)
	public Date bestBefore = new Date();
	
	@FieldOrder(order = 2)
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;
	
	@FieldOrder(order = 3)
	@Scale(value=Consts.SUM_SCALE)
	public int cost = 0;

	@FieldOrder(order = 4)
	public String remark = "";
}
