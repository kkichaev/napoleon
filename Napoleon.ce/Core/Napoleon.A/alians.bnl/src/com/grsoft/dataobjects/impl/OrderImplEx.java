package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		int idx = ((OrderEx)data).whIndex;
		PriceEx pe = (PriceEx)item;
		if(idx == 0 || idx > pe.whQty.size() )
			return pe.qty;
		return pe.whQty.get(idx-1).qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		int idx = ((OrderEx)data).whIndex;
		PriceEx pe = (PriceEx)price.getData();
		if(idx == 0 || idx > pe.whQty.size() )
			super.updatePrice(price, qty);
		else {
			pe.whQty.get(idx-1).qty += qty;
			price.write();
		}
	}
}
