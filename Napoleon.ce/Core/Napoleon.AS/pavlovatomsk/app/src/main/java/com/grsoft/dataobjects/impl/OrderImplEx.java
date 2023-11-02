package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;

import android.util.Log;

public class OrderImplEx extends OrderImpl {
	private SkaldImpl sklad = new SkaldImpl();
	
	@Override
	public int getItemValue(Price item) {
		sklad.read("id", ((OrderEx)data).whCode);
		
		PriceEx pe = (PriceEx)item;
		int whIndex = sklad.getData().whIndex;
		
		if (item.id.equals("√À003928")) {
			Log.d("OrderImplEx", "whIndex: " + whIndex);
			Log.d("OrderImplEx", "item valie: " + Integer.toString(super.getItemValue(item)));
		}
		
		if( whIndex <= 0)
			return super.getItemValue(item);
		
		return whIndex > pe.whQty.size() ?  0 : pe.whQty.get(whIndex-1).qty;
	}
}
