package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgDiscountItem extends DataObject {
	@FieldOrder(order=0)
	public String id="";

	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order=1)
	public int discount = 0;

	@Scale(value=Consts.SUM_SCALE)
	@FieldVersion(version = 1)
	@FieldOrder(order=2)
	public int cost = 0;

}
