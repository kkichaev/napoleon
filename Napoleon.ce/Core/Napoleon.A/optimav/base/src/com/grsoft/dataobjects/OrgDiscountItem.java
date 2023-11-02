package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgDiscountItem extends DataObject {
	@FieldOrder(order=0)
	public int folderID;

	@FieldOrder(order=1)
	public int costype;
	
	@FieldOrder(order=0)
	@Scale(value=Consts.SUM_SCALE)
	public int discount;
}
