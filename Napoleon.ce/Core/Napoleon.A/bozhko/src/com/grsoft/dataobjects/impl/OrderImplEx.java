package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;

public class OrderImplEx extends OrderImpl {
	
	// кол-ва показываем в упаковках 
	@Override
	public int getItemValue(Price item) {
		int res = 0;
		
		if (item.qtyInPack != 0)
			res = (int)((long)item.qty * Consts.QTY_SCALE / item.qtyInPack); 
		
		return res; 
	}
	
	@Override
	protected int checkPriceQty(PriceImpl p, int qty, OrderItem item) {
		int newQty = qty;
		Price pitem = p.getData();
		int whQty = pitem.qty;
		
		
		if( ((CfgNplW)ConfigManager.getConfig()).checkPrice ) {
			int priceQty = whQty;
			if( item != null ) priceQty += item.qty;
			
			if( priceQty < qty ) {
				if( whQty < 0 ) newQty = 0;
				else newQty = priceQty;
			}
		}
		
		return newQty;
	}
	
	@Override
	public int getItemQty(Price item) {
		int val = super.getItemQty(item); 
		return (int)((long)val * Consts.QTY_SCALE / item.qtyInPack);
	}
	
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		boolean ret = super.updateQty(priceImpl, qty, cost, inPack);
		AgentSalesPlanImpl.refreshDocCache();
		return ret;
	}
	
	@Override
	public int count() {
		PriceImpl pi = new PriceImpl();
		pi.setReadingFields("qtyInPack");
		
		Price p = pi.getData();
    	int qty = 0;
    	
    	if( data.items != null )
	    	for(OrderItem item : data.items ) {
	    		p.id = item.id;
	    		int iq = item.qty;
	    		if( pi.read() && p.qtyInPack > 0 )
	    			iq = (int)((long)iq * Consts.QTY_SCALE / p.qtyInPack);
	    		qty += iq;
	    	}
    	
    	pi.close();
    	return qty / Consts.QTY_SCALE;
	}
}

