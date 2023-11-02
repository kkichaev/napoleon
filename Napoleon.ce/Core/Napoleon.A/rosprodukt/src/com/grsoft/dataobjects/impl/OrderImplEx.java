package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceWhData;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		PriceEx pe = (PriceEx)item;
		int whIndex = ((OrderEx)data).whIndex;
		
		if( whIndex <= 0)
			return super.getItemValue(item);
		
		return whIndex > pe.whQty.size() ?  0 : pe.whQty.get(whIndex-1).qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		int idx = ((OrderEx)data).whIndex; 
		if(idx == 0)
			super.updatePrice(price, qty);
		else {
			PriceEx pe = (PriceEx)price.data;
			if( idx-- <= pe.whQty.size() ) { 
				PriceWhData wq = pe.whQty.get(idx);
				wq.qty += qty;
				price.write();
			}
		}
	}
	}
