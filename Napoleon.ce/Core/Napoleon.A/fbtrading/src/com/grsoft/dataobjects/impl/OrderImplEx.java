package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;

public class OrderImplEx extends OrderImpl {

	int curSkladIndex = 0;
	
	public int getItemValue(Price price, int index) {
		PriceEx pe = (PriceEx)price;
		return index == 0 || index > pe.whQty.size() ? price.qty : pe.whQty.get(index-1).qty;
	}
	
	public void setCurSklad(int idx) {
		curSkladIndex = idx;
	}
	
	@Override
	protected int checkPriceQty(PriceImpl p, int qty, OrderItem item) {
		int newQty = qty;			
		if( ((CfgNplW)ConfigManager.getConfig()).checkPrice ) {
			int whQty = getItemValue(p.getData(), curSkladIndex);
			int priceQty = whQty;
			if( item != null ) {
				OrderItemEx oie = (OrderItemEx)item;
				if(curSkladIndex != oie.skladIndex) {
					updatePrice((PriceEx) p.getData(), item.qty, oie.skladIndex);
					item.qty = 0;
				} else {
					priceQty += item.qty;
				}
			}
			
			if( priceQty < qty ) {
				if( whQty < 0 ) newQty = 0;
				else newQty = priceQty;
			}
		}
		
		return newQty;
	}
	
	void updatePrice(PriceEx pe, int qty, int skladIndex) {
		if(skladIndex > 0 && skladIndex <= pe.whQty.size()) {
			int wqty = pe.whQty.get(skladIndex - 1).qty;
			pe.whQty.get(skladIndex - 1).qty = wqty + qty;
		} else {
			pe.qty += qty;
		}
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		updatePrice((PriceEx)price.getData(),qty, curSkladIndex);
		price.write();
	}
	
	@Override
	protected void prepareDeleteItem(PriceImpl pi, OrderItem item) {
		curSkladIndex = ((OrderItemEx)item).skladIndex;
		super.prepareDeleteItem(pi, item);
	}
}
