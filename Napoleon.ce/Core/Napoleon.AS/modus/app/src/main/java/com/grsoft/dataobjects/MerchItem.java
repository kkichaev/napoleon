package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class MerchItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	@Scale(Consts.QTY_SCALE)
	public int start = 0;
	
	@FieldOrder(order=2)
	@Scale(Consts.QTY_SCALE)
	public int finish = 0;
}
