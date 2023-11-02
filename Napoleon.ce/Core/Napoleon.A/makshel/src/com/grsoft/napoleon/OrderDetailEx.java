package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;


public class OrderDetailEx extends OrderDetail {
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}
	
	@Override
	protected void editItem(OrderItem orderItem) {
		OrderItemEx i = (OrderItemEx) orderItem;
		
		if(i.gift.length() == 0)
			super.editItem(orderItem);
	}
	
	@Override
	protected void setAdapter() {
		super.setAdapter();
	}
}
