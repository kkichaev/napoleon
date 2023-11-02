package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgBalance extends DataObject {
	@FieldOrder(order=0)
	@Scale(value=Consts.SUM_SCALE)
	public int sum;
	
	@FieldOrder(order=1)
	@Scale(value=Consts.SUM_SCALE)
	public int sumOut;
}
