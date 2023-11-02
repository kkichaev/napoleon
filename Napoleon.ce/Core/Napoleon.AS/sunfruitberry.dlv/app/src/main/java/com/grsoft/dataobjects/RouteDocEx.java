package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;

public class RouteDocEx extends ItemDef {
	@FieldOrder(order = 100)
	public String userid = "";

	@FieldOrder(order = 101)
	public Date created = new Date();

	@FieldOrder(order = 102)
	public String dlvNumber = "";
}
