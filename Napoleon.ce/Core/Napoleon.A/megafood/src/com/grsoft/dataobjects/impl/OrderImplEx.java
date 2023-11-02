package com.grsoft.dataobjects.impl;

import java.util.HashSet;

import com.grsoft.dataobjects.FocusMatrix;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderRejectItem;
import com.grsoft.dataobjects.impl.OrderImpl;

public class OrderImplEx extends OrderImpl {
	HashSet<String> focusItems = null;
	
	public boolean isValid() {
		OrderEx oe = (OrderEx)data;
		if(oe.needCheckFocusItems == 0)
			return true;
		
		if(focusItems == null)
			focusItems = FocusMatrix.get(this);

		HashSet<String> chk = new HashSet<String>(focusItems);
		for(OrderItem oi : oe.items)
			chk.remove(oi.id);
		
		for(OrderRejectItem ri : oe.rejectItems)
			chk.remove(ri.id);
		
		return chk.size() == 0;
	}
	
	public boolean haveItem(String id) {
		if(findItem(id) != null)
			return true;
		
		for(OrderRejectItem ri : ((OrderEx)data).rejectItems)
			if(ri.id.equals(id)) 
				return true;
		return false;
	}
	
	@Override
	public boolean isEmpty() {
		return ((OrderEx)data).rejectItems.size() == 0 && super.isEmpty();
	}
	
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		if(qty != 0)
			((OrderEx)data).removeRejectItem(priceImpl.getData().id);
		return super.updateQty(priceImpl, qty, cost, inPack);
	}
}
