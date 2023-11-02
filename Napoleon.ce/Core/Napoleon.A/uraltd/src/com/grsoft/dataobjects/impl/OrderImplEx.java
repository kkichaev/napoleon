package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQty;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		Order oe = (Order)data;
		PriceEx pe = (PriceEx)item;
		
		if( oe.supplyer == 0 || oe.supplyer > pe.firmQty.size() )
			return pe.qty;
		
		return pe.firmQty.get(oe.supplyer-1).qty;
	}	

	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		Order oe = (Order)data;
		PriceEx pe = (PriceEx)price.getData();
		
		if( oe.supplyer == 0 )
			super.updatePrice(price, qty);
		else if( oe.supplyer <= pe.firmQty.size() ) {
			pe.firmQty.get(oe.supplyer-1).qty += qty;
			price.write();
		}
	}
	
	@Override
	protected int checkPriceQty(PriceImpl p, int qty, OrderItem item) {
		Order oe = (Order)data;
		PriceEx pe = (PriceEx)p.getData();

		if( oe.supplyer > 0 && oe.supplyer <= pe.firmQty.size() ) {
			int newQty = qty;
			
			if( ((CfgNpl)ConfigManager.getConfig()).checkPrice ) {
				PriceQty data = pe.firmQty.get(oe.supplyer-1); 
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
}
