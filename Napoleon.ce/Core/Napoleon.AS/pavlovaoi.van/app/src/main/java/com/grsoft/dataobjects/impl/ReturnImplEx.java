package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Price;


public class ReturnImplEx extends ReturnImpl {
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		price.getData().vanQty += qty;
		price.write();
	}
	
	@Override
	public int getItemValue(Price item) {
		return item.vanQty;
	}
}
