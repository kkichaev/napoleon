package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.types.FieldOrder;

public class ReturnItem extends OrderItem {
	@FieldOrder(order=OrderItem.USER_FIELDS)
	public String cause;
	
	@FieldOrder(order=OrderItem.USER_FIELDS + 1)
	public String dlvNum;
	
	@FieldOrder(order=OrderItem.USER_FIELDS + 2)
	public Date dlvDate;
}
