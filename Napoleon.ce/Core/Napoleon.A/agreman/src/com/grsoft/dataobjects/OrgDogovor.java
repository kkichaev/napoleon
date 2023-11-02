package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgDogovor extends DataObject {
	@FieldOrder(order=0)
	public String id;

	@FieldOrder(order=1)
	public String name;

	@FieldOrder(order=2)
	@Scale(value=Consts.SUM_SCALE)
	public int limit;
	
	@FieldOrder(order=3)
	public int costype;
}
