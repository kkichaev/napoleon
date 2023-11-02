package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;

public class DeliverItemEx extends DeliveryItem {
	@FieldOrder(order=10)
	public Date bestBefore = new Date();	
}
