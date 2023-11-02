package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.util.Consts;


public class DeliveryImplEx extends DeliveryImpl {
	public int countPack() {
    	int qty = 0;
    	
    	if( data.items != null ) {
			PriceImpl p = new PriceImpl();
			p.setReadingFields("qtyInPack");
			
			PricePrint pd = (PricePrint) p.getData();
			for (DeliveryItem item: data.items) {
				pd.id = item.id;
				
				if( p.read() ) {
					int qip = (pd.qtyInPack == 0) ? Consts.QTY_SCALE : pd.qtyInPack;
					qty += (int)((long)item.qty * Consts.QTY_SCALE / qip);
				}
			}
			p.close();
    	}
    	
    	return qty / Consts.QTY_SCALE;
	}
	
	public int count() {
    	int qty = 0;
    	
    	if( data.items != null )
	    	for(DeliveryItem item : data.items )
	    		qty += item.qty;
    	
    	return qty / Consts.QTY_SCALE;
    }
}
