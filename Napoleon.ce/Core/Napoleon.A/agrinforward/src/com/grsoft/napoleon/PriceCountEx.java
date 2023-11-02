package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import android.os.Bundle;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	
	int discount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(document instanceof OrderImpl)
			((OrderImpl)document).setUpdateQtyHandler(this);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		discount = ((CostStrategyEx)CostStrategy.defaultInstance).getDiscount(price.getData(), document);
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		((OrderItemEx)item).costWO = item.cost + discount;		
	}
}
