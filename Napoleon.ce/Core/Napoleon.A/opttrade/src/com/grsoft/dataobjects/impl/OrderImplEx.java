package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;

public class OrderImplEx extends OrderImpl {
	int whIndex = -1; 
	
	@Override public int getItemValue(Price item) { return SkladHelper.getItemValue(item, whIndex, ((OrderEx)data).whCode); }
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		if( whIndex == -1 ) 
			whIndex = SkladHelper.getWhIndex(((OrderEx)data).whCode);

		PriceEx pe = (PriceEx)price.getData();
		if( whIndex == 0 )
			super.updatePrice(price, qty);
		else if( whIndex <= pe.whQty.size() ) {
			pe.whQty.get(whIndex-1).qty += qty;
			price.write();
		}
	}

	public void resetSklad() {
		whIndex = -1;
	}
}
