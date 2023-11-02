package com.grsoft.dataobjects.impl;

import java.util.ArrayList;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class OrderImplEx extends OrderImpl {
	
	int whIndex = -1;
	public int getWhIndex() {
		if( whIndex < 0 ) {
			ConfigImpl ci = new ConfigImpl();
			Config c = ci.getData();
			c.key = "Склады";
			if( ci.read() )
				whIndex = DialogHelper.makeListWithKey(c.value, new ArrayList<KeyValue>(), ((OrderEx)data).whCode);
			ci.close();
			
			if( whIndex < 0 )
				whIndex = 0;
		}
		
		return whIndex;
	}
	
	public void setWhIndex(int newIndex) {
		whIndex = newIndex;
	}
	
	@Override
	public int getItemValue(Price item) {
		int wi = getWhIndex();
		if( wi <= 0 || wi > ((PriceEx)item).qtys.size() ) return item.qty;
		return ((PriceEx)item).qtys.get(wi-1).qty;
	}

	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		PriceEx pe = (PriceEx)price.data;
		int wi = getWhIndex();
		if( wi <= 0 || wi > pe.qtys.size() )
			super.updatePrice(price, qty);
		else {
			pe.qtys.get(wi-1).qty += qty;
			price.write();
		}
	}
}
