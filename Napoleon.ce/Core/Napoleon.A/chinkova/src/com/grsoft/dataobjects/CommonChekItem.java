package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;

public class CommonChekItem extends CommonIncassItem {
	@FieldOrder(order=100)
	public Date created = new Date();
}
