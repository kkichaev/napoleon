package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		return ((OrderEx)data).whIndex == 0 ? item.qty : ((PriceEx)item).qty2;
	}
}
