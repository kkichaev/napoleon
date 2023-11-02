package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.napoleon.PriceCountEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;

public class OrderImplEx extends OrderImpl {
	
	@Override
	public int getItemValue(Price item) {
		OrderEx oe = (OrderEx)data;
		PriceEx pe = (PriceEx)item;
		
		if( oe.whIndex <= 0 || oe.whIndex > pe.whQty.size() )
			return pe.qty;
		
		return pe.whQty.get(oe.whIndex-1).qty;
	}	

	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		OrderEx oe = (OrderEx)data;
		PriceEx pe = (PriceEx)price.getData();
		
		if( oe.whIndex <= 0 )
			super.updatePrice(price, qty);
		else if( oe.whIndex <= pe.whQty.size() ) {
			pe.whQty.get(oe.whIndex-1).qty += qty;
			price.write();
		}
	}
	
	@Override
	protected int checkPriceQty(PriceImpl p, int qty, OrderItem item) {
		OrderEx oe = (OrderEx)data;
		PriceEx pe = (PriceEx)p.getData();

		if( oe.whIndex > 0 && oe.whIndex <= pe.whQty.size() ) {
			int newQty = qty;
			
			if( ((CfgNpl)ConfigManager.getConfig()).checkPrice ) {
				PriceQtyItem data = pe.whQty.get(oe.whIndex-1); 
				int priceQty = data.qty;
				if( item != null ) priceQty += item.qty;
				
				if( priceQty < qty ) {
					if( data.qty < 0 ) newQty = 0;
					else newQty = priceQty;
				}
			}
			
			return newQty;
		}
		return super.checkPriceQty(p, qty, item);
	}
	
	public void setWh(int newWh) {
		if( newWh >= 0 )
			((OrderEx)data).whIndex = newWh;
	}
	
	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCountEx.open(context, itemRowid, this);
	}
}
