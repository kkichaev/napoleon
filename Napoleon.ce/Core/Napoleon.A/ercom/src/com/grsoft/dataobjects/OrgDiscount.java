package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgDiscount extends DataObject {
	@FieldOrder(order=0)
	public int id; 

	@FieldOrder(order=1)
	@Scale(value=Consts.SUM_SCALE)
	public int discount;
}
