package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;

public class ReturnItemEx extends OrderItem {
	@FieldOrder(order = USER_FIELDS )
	public Date mfrDate;

	@FieldOrder(order = USER_FIELDS + 1)
	public Date endDate;
	
	@FieldOrder(order = USER_FIELDS + 2)
	public String remark;
}
