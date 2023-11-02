package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;

public class VisitItemEx extends VisitItem {
	@FieldOrder(order=10)
	@FieldVersion(version = 1)
	public String script = "";
}
