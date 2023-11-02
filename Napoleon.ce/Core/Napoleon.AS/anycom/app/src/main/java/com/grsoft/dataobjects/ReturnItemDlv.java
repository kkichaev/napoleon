package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class ReturnItemDlv extends DataObject {
	@FieldOrder(order=0)
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
	
	@FieldOrder(order=1)
	public String number;
	
	@FieldOrder(order=2)
	public Date date;

	@FieldOrder(order=3)
	@Scale(value=Consts.SUM_SCALE)
	public int cost;

	@FieldOrder(order=4)
	public String uid = "";
}
