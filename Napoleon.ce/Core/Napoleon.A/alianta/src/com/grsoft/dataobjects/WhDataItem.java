package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class WhDataItem extends DataObject {
	@FieldOrder(order=0)
	public String cfo = "";

	@Scale(value=Consts.SUM_SCALE)
	@FieldOrder(order=0)
	public int cost = 0;
}
