package com.grsoft.dataobjects.impl;

import java.util.List;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQty;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		int index = ((OrderEx)data).whIndex;
		List<PriceQty> whQty = ((PriceEx)item).whQty;
		
		return ( index == 0 || index > whQty.size() ) ?  item.qty : whQty.get(index-1).qty;
	}
}
