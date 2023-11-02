package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	public String action = "";
	public String actionItem = "";
	
	@Scale(value = Consts.SUM_SCALE)
	public int actionSum = 0;
	
	public void removeAction() {
		action = "";
		actionItem = "";
		actionSum = 0;
		for(OrderItem oi : items) {
			oi.cost = ((OrderItemEx)oi).costWD;
		}
	}

	public void setAction(Action act) {
		action = act.id;
		actionItem = act.item;
		
		OrderItemEx pos = (OrderItemEx) findItem(act.item);
		if(pos != null)
			act.distributeDiscount(this, pos);
	}
}
