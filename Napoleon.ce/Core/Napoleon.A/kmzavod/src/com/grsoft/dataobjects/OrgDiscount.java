package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgDiscount extends DataObject {
	@FieldOrder(order = 0)
	public String dogovor = "";

	@FieldOrder(order = 1)
	public String id = "";
	
	@FieldOrder(order = 2)
	@Scale(value = Consts.SUM_SCALE)
	public int discount = 0;
}
