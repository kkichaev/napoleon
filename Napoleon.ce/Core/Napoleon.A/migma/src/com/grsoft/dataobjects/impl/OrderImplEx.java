package com.grsoft.dataobjects.impl;

import java.util.List;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQty;
import com.grsoft.util.Util;

public class OrderImplEx extends OrderImpl implements OrderImplBase.UpdateQtyHandler {
	
	
	public OrderImplEx() {
		setUpdateQtyHandler(this);
	}
	
	@Override
	public int getItemValue(Price item) {
		int index = ((OrderEx)data).whIndex;
		List<PriceQty> whQty = ((PriceEx)item).whQty;
		
		return ( index == 0 || index > whQty.size() ) ?  item.qty : whQty.get(index-1).qty;
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx i = (OrderItemEx) item;
		
		if (isNewItem)
			i.i_cr_tm = Util.getDateTime();
		
		i.i_mod_tm = Util.getDateTime();
	}
}
