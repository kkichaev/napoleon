package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceQtyItem;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		Price pe = item;
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
			Price pe = (Price)price.data;
			if( idx-- <= pe.whQty.size() ) { 
				PriceQtyItem wq = pe.whQty.get(idx);
				wq.qty += qty;
				price.write();
			}
		}
	}
	}
