package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected long getItemSum(OrderItem item) {
		if(((OrderEx)doc.getData()).fromKIS != 0) {
			return ((OrderItemEx)item).sum;
		}
		return super.getItemSum(item);
	}
	
	@Override
	protected void init() {
		btnSend.setEnabled(((OrderEx)doc.getData()).fromKIS == 0);
	}
}
