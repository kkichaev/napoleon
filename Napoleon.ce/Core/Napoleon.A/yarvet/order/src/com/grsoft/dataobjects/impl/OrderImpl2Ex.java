package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Order2Ex;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Price2Ex;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;

public class OrderImpl2Ex extends OrderImplEx {
	PriceImpl price = new PriceImpl();
	
	@Override
	public int getItemValue(Price item) {
		Price2Ex pe = (Price2Ex)item;
		int whIndex = ((Order2Ex)data).whIndex;
		
		if( whIndex <= 0)
			return super.getItemValue(item);
		
		return whIndex > pe.whQty.size() ?  0 : pe.whQty.get(whIndex-1).qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		int whIndex = ((Order2Ex)data).whIndex;
		
		Price2Ex pe = (Price2Ex)price.getData();
		if( whIndex == 0 )
			super.updatePrice(price, qty);
		else if( whIndex <= pe.whQty.size() ) {
			pe.whQty.get(whIndex-1).qty += qty;
			price.write();
		}
	}

	public long getRentability(String id) {
		long res = 0;
		
		OrderItem i = (OrderItem) findItem(id);
		
		if (i != null) {
			if (price.read("id", id)) {
				long c = i.cost - ((PriceEx)price.getData()).minCost;
				
				if (i.cost != 0)
					res = (long) ((double)c / i.cost * 100 * Consts.SUM_SCALE);
			}
		}
		
		return res;
	}
}
