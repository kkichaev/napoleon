package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;

public class ReturnItem extends OrderItem {
	@FieldOrder(order=100)
	public String discid = "";

	@FieldOrder(order=101)
	public String number = "";

	@FieldOrder(order=102)
	public Date date;
}
