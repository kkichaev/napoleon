package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;

public class DeliveryItemEx extends DeliveryItem {
	@FieldOrder(order=10)
	public String party = "";

	@FieldOrder(order=11)
	public Date expired = new Date();
}
