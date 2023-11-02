package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;

public class OrderImplEx extends OrderImpl {
	public long sumDisc() {
		long res = 0;
		
		if(data.items != null) {
			for (OrderItem oi: data.items) {
				res += (long)((OrderItemEx)oi).disc * oi.qty / Consts.QTY_SCALE;
			}
		}
		
		return res;
	}
	
	@Override
	public int getItemValue(Price item) {
		PriceEx pe = (PriceEx)item;
		int whIndex = ((OrderEx)data).whIndex;
		
		if( whIndex <= 0 || whIndex > pe.whQty.size())
			return super.getItemValue(item);
		
		return pe.whQty.get(whIndex-1).qty;
	}

}
