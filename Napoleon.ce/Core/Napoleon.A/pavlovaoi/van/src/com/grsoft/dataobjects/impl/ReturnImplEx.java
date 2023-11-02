package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrint;


public class ReturnImplEx extends ReturnImpl {
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		((PricePrint)price.getData()).vanQty += qty;
		price.write();
	}
	
	@Override
	public int getItemValue(Price item) {
		return ((PricePrint)item).vanQty;
	}
}
