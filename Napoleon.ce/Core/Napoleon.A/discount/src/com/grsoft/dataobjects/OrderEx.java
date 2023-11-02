package com.grsoft.dataobjects;


public class OrderEx extends Order {
	public String whCode = "";
	public int whIndex = -1;
	
	public String action = "";
	public long discountSum = 0;
	
	public void updateAction(ActionWithText act) {		
		for(OrderItem oi : items) {
			((OrderItemEx)oi).discount = 0;
		}

		if(act != null) {
			discountSum = act.getOrderDiscountSum();
			action = act.id;
			((OrderItemEx)act.getAffectedItem()).discount = discountSum;
		} else {
			discountSum = 0;
			action = "";
		}
	}
	
	@Override
	public long sum() {
		return super.sum() - discountSum;
	}
}
