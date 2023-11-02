package com.grsoft.dataobjects.impl;

import java.util.List;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceWhData;

public class OrderImplEx extends OrderImpl {
	
	@Override
	public int getItemValue(Price item) {
		int index = ((OrderEx)data).whIndex;
		List<PriceWhData> whQty = ((PriceEx)item).whQty;
		
		return ( index == 0 || index > whQty.size() ) ?  item.qty : whQty.get(index-1).qty;
	}

	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		int index = ((OrderEx)data).whIndex;
		if(index == 0)
			super.updatePrice(price, qty);
		else
		{
			List<PriceWhData> whQty = ((PriceEx)price.getData()).whQty;
			if(index <= whQty.size())
				whQty.get(index - 1).qty += qty;
			price.write();
		}
	}
}
