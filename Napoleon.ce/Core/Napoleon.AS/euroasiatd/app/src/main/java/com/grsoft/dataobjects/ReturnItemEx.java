package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;

public class ReturnItemEx extends OrderItem {
	@FieldOrder(order = USER_FIELDS)
	public Date bestBefore = new Date();
	public String qual = "";
}
