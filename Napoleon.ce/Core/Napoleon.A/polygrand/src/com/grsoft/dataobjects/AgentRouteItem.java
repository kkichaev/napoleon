package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class AgentRouteItem extends DataObject {
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	public int isNew = 0;
	
	@FieldOrder(order=2)
	public String task = "";
}
