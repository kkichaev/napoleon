package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.napoleon.modules.print.util.DocNumberStrategy.ISupplyer;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class OrderImplEx extends OrderImpl implements ISklad, ISupplyer {
	int whIndex = -1; 
	
	public static int getWhIndex(String whCode) {
		int index = -1;
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = "Склады";
		if(ci.read()) {
			ArrayList<KeyValue> values = new ArrayList<KeyValue>();
			index = DialogHelper.makeListWithKey(c.value, values, whCode);
		}
		ci.close();
		
		if( index < 0 )
			index = 0;
		
		return index;
	}
	
	public int getWhIndex() {
		if( whIndex == -1 ) 
			whIndex = getWhIndex(((OrderEx)data).whCode);
		return whIndex;
	}
	
	@Override
	public int getItemValue(Price item) {
		if( whIndex == -1 ) 
			whIndex = getWhIndex(((OrderEx)data).whCode);

		List<PriceQtyItem> whQty = ((PriceEx)item).whQty;
		
		return ( whIndex == 0 || whIndex > whQty.size() ) ?  item.qty : whQty.get(whIndex-1).qty;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		if( whIndex == -1 ) 
			whIndex = getWhIndex(((OrderEx)data).whCode);

		PriceEx pe = (PriceEx)price.getData();
		if( whIndex == 0 )
			super.updatePrice(price, qty);
		else if( whIndex <= pe.whQty.size() ) {
			pe.whQty.get(whIndex-1).qty += qty;
			price.write();
		}
	}

	@Override
	public String getSupplyer() {return ((OrderEx)data).firmCode; }
}
