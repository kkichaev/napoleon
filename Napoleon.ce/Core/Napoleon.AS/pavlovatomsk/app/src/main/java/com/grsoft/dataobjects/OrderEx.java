package com.grsoft.dataobjects;

public class OrderEx extends Order{
	public int bonus = 0;

	public boolean needDecision() {
		for(OrderItem oi : items) {
			OrderItemEx oid = (OrderItemEx) oi;
			if(oid.discount > 0 && oid.actionGift == 0)
				return true;
		}
		return false;
	}
	public int discountDecision = 0;
}
