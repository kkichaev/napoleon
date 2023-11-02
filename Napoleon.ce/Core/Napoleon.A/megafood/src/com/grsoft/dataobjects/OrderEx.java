package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrderEx extends Order {
	public int curMatrix = -1;
	public int needCheckFocusItems = 0;
	
	public List<OrderRejectItem> rejectItems = new ArrayList<OrderRejectItem>();

	public OrderRejectItem findRejectItem(String id) {
		for(OrderRejectItem ori : rejectItems)
			if(ori.id.equals(id))
				return ori;
		return null;
	}

	public void removeRejectItem(String id) {
		for(OrderRejectItem ori : rejectItems)
			if(ori.id.equals(id)) {
				rejectItems.remove(ori);
				break;
			}
	}
	
	public void addRejectItem(String id, String reason) {
		OrderRejectItem ori = new OrderRejectItem();
		ori.id = id;
		ori.reason = reason;
		rejectItems.add(ori);
		
		for(OrderItem oi : items)
			if(oi.id.equals(id)) {
				items.remove(oi);
				break;
			}
	}
}
