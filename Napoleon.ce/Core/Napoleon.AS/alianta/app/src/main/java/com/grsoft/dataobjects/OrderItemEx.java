package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.FieldOrder;

public class OrderItemEx extends OrderItem {
	@FieldOrder(order=USER_FIELDS)
	public List<OrderWhItem> whData = new ArrayList<OrderWhItem>();
	
	@FieldOrder(order=USER_FIELDS + 1)
	public String action= "";
	
	@FieldOrder(order=USER_FIELDS + 2)
	public String uid = "";
}
