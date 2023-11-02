package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.ExtrasConst;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		PriceEx pe = (PriceEx)item;
		int whIndex = ((OrderEx)data).whIndex;
		
		if( whIndex <= 0 || whIndex > pe.whQty.size())
			return super.getItemValue(item);
		
		return pe.whQty.get(whIndex-1).qty;
	}

	public long insert() {
		rowid = ExtrasConst.INVALID_ROWID;
		write();
		close();
		return rowid;
	}
}
