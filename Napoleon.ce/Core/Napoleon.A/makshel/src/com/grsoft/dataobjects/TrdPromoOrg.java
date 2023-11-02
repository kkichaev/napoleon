package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class TrdPromoOrg extends DataObject {
	public static final int ALL_REL = 0;
	public static final int GROUP_REL = 1;
	public static final int ORG_REL = 2;	
	
	@FieldOrder(order = 0)
	public int rel = 0;

	@FieldOrder(order = 1)
	public String code = "";
}
