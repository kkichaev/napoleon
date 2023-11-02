package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;

public class VisitItemEx extends VisitItem {
	@FieldVersion(version = 1)
	@FieldOrder(order = 10)
	public String itemId = "";

	@FieldVersion(version = 1)
	@FieldOrder(order = 11)
	public String orgId = "";
}
