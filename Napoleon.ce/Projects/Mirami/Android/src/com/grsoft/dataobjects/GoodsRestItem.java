package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class GoodsRestItem extends DataObject {

	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	@Scale(value=Consts.QTY_SCALE)
	public int qty;

	@FieldOrder(order=2)
	@Scale(value=Consts.QTY_SCALE)
	public int vqty;
	
	@FieldOrder(order=3)
	public Date date;
}
