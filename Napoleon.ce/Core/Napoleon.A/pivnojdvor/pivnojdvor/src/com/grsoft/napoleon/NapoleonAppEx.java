package com.grsoft.napoleon;

import com.grsoft.dataobject.OrderItemEx;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;

public class NapoleonAppEx extends NapoleonApp {
	@Override
	protected void defineNewType() {
		super.defineNewType();
		
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		
		OrderDetail.activity = OrderDetail2Ex.class;
		PriceCount.activity = PriceCount2Ex.class;
		
	}
	
}
