package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;
import com.grsoft.util.Util;

public class ReturnItem extends OrderItem {
	@FieldOrder(order=OrderItem.USER_FIELDS)
	public String cause = "";

	@FieldOrder(order=OrderItem.USER_FIELDS + 1)
	public String turn = "";

	@FieldOrder(order=OrderItem.USER_FIELDS + 2)
	public Date prdDate = new Date();
	
	@FieldOrder(order=OrderItem.USER_FIELDS + 3)
	public int num = 0;
	
	public String getKey() {
		return id + cause + turn + Long.toHexString(Util.getDayStart(prdDate).getTime());
	}
}
