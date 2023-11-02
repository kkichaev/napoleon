package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;

public class ReturnItemEx extends OrderItem {
	@FieldOrder(order=OrderItem.USER_FIELDS)
	public String cause = "";
	
	@FieldOrder(order=OrderItem.USER_FIELDS + 1)
	public Date dlvDate = new Date();
	
	@FieldOrder(order=OrderItem.USER_FIELDS + 2)
	public String dlvNumber = "";
}
