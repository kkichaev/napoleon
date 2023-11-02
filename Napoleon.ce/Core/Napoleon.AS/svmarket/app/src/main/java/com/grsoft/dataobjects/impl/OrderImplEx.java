package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		PriceEx pe = (PriceEx)item;
		int whIndex = data.supplyer;
		
		if( whIndex <= 0 || whIndex > pe.whQty.size())
			return super.getItemValue(item);
		
		return pe.whQty.get(whIndex-1).qty;
	}

	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		if (data.supplyer > 0) {
			PriceEx pe = (PriceEx) price.getData();
			if(pe.whQty.size() >= data.supplyer) {
				pe.whQty.get(data.supplyer - 1).qty += qty;
				price.write();
			}
		} else
			super.updatePrice(price, qty);
	}
}
