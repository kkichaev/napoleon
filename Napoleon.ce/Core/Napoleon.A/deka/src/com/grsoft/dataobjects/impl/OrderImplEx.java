package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceItem;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		int result = 0;
		
		for(PriceItem i : ((PriceEx)item).items)
			if (i.id.equals(((OrderEx)data).distr)){
				result = i.qty;
				break;
			}
			
		return result;
	}
}
