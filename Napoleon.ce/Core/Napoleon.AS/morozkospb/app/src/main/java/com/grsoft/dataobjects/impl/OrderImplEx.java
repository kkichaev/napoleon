package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.StoreHelper;

public class OrderImplEx extends OrderImpl {
	
	@Override
	public int getItemValue(Price item) {
		return StoreHelper.getQty(((OrderEx)data).idStore, item.id);
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		StoreHelper.updateQty(((OrderEx)data).idStore, price.getData().id, qty);
	}
}
