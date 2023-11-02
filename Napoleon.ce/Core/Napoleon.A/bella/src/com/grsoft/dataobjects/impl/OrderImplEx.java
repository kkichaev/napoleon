package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		PriceEx pe = (PriceEx)item;
		int whIndex = ((OrderEx)data).whIndex;
		
		if( whIndex == 0 )
			return super.getItemValue(item);
		if( whIndex < 0 || whIndex > pe.whQty.size())
			return 0;
		
		return pe.whQty.get(whIndex-1).qty;
	}
}
