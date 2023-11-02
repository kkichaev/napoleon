package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;

public class OrderItemEx extends OrderItem implements Unitable {
	@FieldOrder(order=USER_FIELDS)
	public String unit = "";
	
	/*****************
	 * Для возвратов *
	 ****************/
	/***
	 * Партия
	 */
	@FieldOrder(order=USER_FIELDS+1)
	public Date party;
	
	/***
	 * Причина возврата
	 */
	@FieldOrder(order=USER_FIELDS+2)
	public String cause;

	@Override
	public String getUnit() {
		return unit;
	}

	@Override
	public void setUnit(String id) {
		unit = id;
	}
}
