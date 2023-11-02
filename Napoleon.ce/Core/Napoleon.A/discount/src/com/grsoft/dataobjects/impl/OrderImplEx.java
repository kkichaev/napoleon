package com.grsoft.dataobjects.impl;

import java.util.HashMap;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.SkladItem;
import com.grsoft.napoleon.CostStrategyEx;

public class OrderImplEx extends OrderImpl{
	
	public int getItemValue(Price item) {
		
		HashMap<String, SkladItem> sklData = CostStrategyEx.getSkaldData(((OrderEx)data).whCode);
		SkladItem si = sklData.get(item.id);
		return si != null ? si.qty : 0;
	};
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		
		OrderEx od = (OrderEx)data;
		String itemId = price.getData().id;
		
		HashMap<String, SkladItem> sklData = CostStrategyEx.getSkaldData(od.whCode);
		SkladItem si = sklData.get(itemId);
		if(si != null) {
			SkladItemImpl sii = new SkladItemImpl();
			SkladItem sdata = sii.getData();
			sdata.id = od.whCode;
			sdata.id_i = itemId;
			if( sii.read() ) {
				sdata.qty += qty;
				si.qty = sdata.qty;
				sii.write();
			}
			sii.close();
		} else
			super.updatePrice(price, qty);
	}
	
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		((OrderEx)data).updateAction(null);
		return super.updateQty(priceImpl, qty, cost, inPack);
	}
}
