package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;

public class OrderBundleItem extends DataObject {
	@FieldOrder(order = 0)
	public Date created;
	
	public OrderBundleItem() {
		created = new Date();
	}
	
	public OrderBundleItem(Order doc) {
		created = doc.created;
	}
}
