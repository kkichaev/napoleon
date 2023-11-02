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
	
	int getWhIndex() {
		int index = -1;
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = "Склады";
		if(ci.read()) {
			ArrayList<KeyValue> values = new ArrayList<KeyValue>();
			index = DialogHelper.makeListWithKey(c.value, values, ((OrderEx)data).whCode);
		}
		ci.close();
		
		if( index < 0 )
			index = 0;
		
		return index;
	}
	
	@Override
	public int getItemValue(Price item) {
		if( whIndex == -1 ) 
			whIndex = getWhIndex();

//		if( whIndex == 0 )
//			return item.qty;
		
		return (whIndex < ((PriceEx)item).whQty.size()) ? 
				((PriceEx)item).whQty.get(whIndex).qty : 
				0;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		if( whIndex == -1 ) 
			whIndex = getWhIndex();

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
