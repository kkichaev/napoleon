package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class RemnantItemEx extends RemnantItem {
	
	@FieldOrder(order=10)
	public int format = 0;
	
	@FieldOrder(order=11)
	@Scale(Consts.QTY_SCALE)
	public int face = 0;
	
	@FieldOrder(order=12)
	@Scale(Consts.SUM_SCALE)
	public int cost = 0;
	
	@FieldOrder(order=13)
	public int promo = 0;
	
	@FieldOrder(order=14)
	public String oos = "";
	
	@FieldOrder(order=15)
	public String remark = "";

}
