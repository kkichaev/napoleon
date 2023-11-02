package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderItemEx extends OrderItem {
	@FieldOrder(order = USER_FIELDS)
	public String discount = "";
	
	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order = USER_FIELDS+1)
	public int dscValue = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order = USER_FIELDS+2)
	public int costWOD = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order = USER_FIELDS+3)
	public int addDscValue = 0;

	@FieldOrder(order = USER_FIELDS+4)
	public int usePriceCost = 0;

	@FieldOrder(order = USER_FIELDS + 5)
	public String addDiscount = "";
	
	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order = USER_FIELDS+6)
	public int costOrg = 0;
}
