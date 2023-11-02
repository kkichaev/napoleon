package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;

public class OrderImplEx extends OrderImpl {
	
//	public int getCheckValue(Price item) {
//		int value2 = ((PriceEx)item).qty2;
//		int value = item.qty;
//		
//		if( ((OrderEx)data).useSecondWH() )
//			value = value2;
//		else
//			value += value2;
//		return value;
//	}
//	
//	@Override
//	protected int checkPriceQty(PriceImpl p, int qty, OrderItem item) {
//		int newQty = qty;
//		int whQty = getCheckValue(p.getData());
//		
//		if( ((CfgNplW)ConfigManager.getConfig()).checkPrice ) {
//			int priceQty = whQty;
//			if( item != null ) priceQty += item.qty;
//			
//			if( priceQty < qty ) {
//				if( whQty < 0 ) newQty = 0;
//				else newQty = priceQty;
//			}
//		}
//		
//		return newQty;
//	}
//	
//
//	@Override
//	protected void updatePrice(PriceImpl price, int qty) {
//		PriceEx pe = (PriceEx)price.getData();
//		if(((OrderEx)data).useSecondWH())
//			pe.qty2 += qty;
//		else {
//			pe.qty += qty;
//			if( pe.qty < 0 && qty < 0 ) {
//				pe.qty2 += pe.qty;
//				pe.qty = 0;
//			}
//		}
//		price.write();
//	}
}
