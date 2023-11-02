package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.WhQty;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		PriceEx pe = (PriceEx)item;
		int idx = ((OrderEx)data).whIndex; 
		if( idx > 0 && pe.whQty.size() >= idx )
			return pe.whQty.get(idx-1).qty;
		return pe.qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		PriceEx pe = (PriceEx)price.getData();
		int idx = ((OrderEx)data).whIndex; 
		if( idx > 0 && pe.whQty.size() >= idx ) {
			idx--;
			WhQty q = pe.whQty.get(idx);
			q.qty += qty;
			pe.whQty.set(idx, q);
			price.write();
		} else
			super.updatePrice(price, qty);
	}
}
