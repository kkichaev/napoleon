package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;

public class PPayItem extends DataObject {
	@FieldOrder(order=0)
	public String number;

	@FieldOrder(order=1)
	public Date date;
}
