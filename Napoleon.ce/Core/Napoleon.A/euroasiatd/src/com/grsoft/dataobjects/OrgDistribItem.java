package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgDistribItem extends RemnantItem {
	@FieldOrder(order=100)
	@Scale(value=Consts.SUM_SCALE)
	public int cost;
	
	@FieldOrder(order=101)
	public String remark;
	
	@FieldOrder(order=102)
	public int action = 0;
}
