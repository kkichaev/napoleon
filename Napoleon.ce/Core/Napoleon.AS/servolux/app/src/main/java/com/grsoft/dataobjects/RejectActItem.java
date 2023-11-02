package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;

public class RejectActItem extends OrderItem {
	@FieldOrder(order = USER_FIELDS)
	public String number = "";

	@FieldOrder(order = USER_FIELDS + 1)
	public String remark = "";
	
	@FieldOrder(order = USER_FIELDS + 2)
	public Date date = new Date();

	@FieldOrder(order = USER_FIELDS + 3)
	public String party = "";

	@FieldOrder(order = USER_FIELDS + 4)
	public Date expired = new Date();
}
