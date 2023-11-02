package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgActionItem extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";

	@FieldOrder(order = 1)
	public Date start = new Date();

	@FieldOrder(order = 2)
	public Date end = new Date();
	
	@FieldOrder(order = 3)
	@Scale(value=Consts.SUM_SCALE)
	public int cost;
}
