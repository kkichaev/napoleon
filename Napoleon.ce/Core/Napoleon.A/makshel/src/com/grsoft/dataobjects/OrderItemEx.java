package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderItemEx extends OrderItem {
	
	static public final int ACTION_ITEM = 0x10; 
	
	@FieldOrder(order=USER_FIELDS+1)
	public String gift = "";
	
	@FieldOrder(order=USER_FIELDS+2)
	@Scale(value=Consts.SUM_SCALE)
	public int disc = 0;
	
	@FieldOrder(order=USER_FIELDS+3)
	@Scale(value=Consts.SUM_SCALE)
	public int maxdisc = 0;
	
	@FieldOrder(order=USER_FIELDS+4)
	public int idTrd = 0;

	@FieldOrder(order=USER_FIELDS+5)
	@Scale(value=Consts.SUM_SCALE)
	public int priceCost = 0;
	
	public boolean IsActionItem() { return ((flags & ACTION_ITEM) != 0); }
	
	public OrderItemEx() {}
	
	public OrderItemEx(ActionDataItem src) {
		cost = src.cost;
		disc = src.dsc;
		flags = OrderItemEx.ACTION_ITEM;
		id = src.priceId;
		idTrd = src.promoId;
		priceCost = src.priceCost;
		qty = src.qty;
	}
}
