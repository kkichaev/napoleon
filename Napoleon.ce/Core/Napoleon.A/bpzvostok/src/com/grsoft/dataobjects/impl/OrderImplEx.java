package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		PriceEx pe = (PriceEx)item;
		int whIndex = ((OrderEx)data).whIndex - 1;
		
		if( whIndex <= 0 || whIndex > pe.whQty.size())
			return super.getItemValue(item);
		
		return pe.whQty.get(whIndex-1).qty;
	}
	
	public int getItem2Value(Price item) {
		int res = super.getItemValue(item);
		
		PriceEx pe = (PriceEx)item;
		int whIndex = ((OrderEx)data).whIndex - 1;
		
		for (int i = 0; i < pe.whQty.size(); i++)
			if (i + 1 != whIndex)
				res = pe.whQty.get(i).qty;
		
		return res;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		int whIndex = ((OrderEx)data).whIndex - 1;

		PriceEx pe = (PriceEx)price.getData();
		if( whIndex == 0 )
			super.updatePrice(price, qty);
		else if( whIndex <= pe.whQty.size() ) {
			pe.whQty.get(whIndex-1).qty += qty;
			price.write();
		}
	}	
}
