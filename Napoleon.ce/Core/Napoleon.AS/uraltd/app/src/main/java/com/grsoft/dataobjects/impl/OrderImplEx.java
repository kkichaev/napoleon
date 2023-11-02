package com.grsoft.dataobjects.impl;

import java.util.List;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQtyItem;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		int whIndex = ((OrderEx)data).whIndex;
		List<PriceQtyItem> whQty = ((PriceEx)item).whQty;
		
		return ( whIndex == 0 || whIndex > whQty.size() ) ?  item.qty : whQty.get(whIndex-1).qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		int whIndex = ((OrderEx)data).whIndex;

		PriceEx pe = (PriceEx)price.getData();
		if( whIndex == 0 )
			super.updatePrice(price, qty);
		else if( whIndex <= pe.whQty.size() ) {
			pe.whQty.get(whIndex-1).qty += qty;
			price.write();
		}
	}
}
