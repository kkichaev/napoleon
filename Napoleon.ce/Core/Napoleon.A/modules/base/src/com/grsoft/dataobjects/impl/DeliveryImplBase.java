package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;

// вынес вес в общий класс надо для возвратов из 1с в АльянсПродкут
public abstract class DeliveryImplBase<T extends Delivery> extends Document<Delivery> {
	@Override
	public long sum() { return data.sum(); }

	public int weight() {
		int res = 0;
		
		if( data.items != null ) {
			PriceImpl p = new PriceImpl();
			Price pd = p.getData();
			for (DeliveryItem item: data.items) {
				pd.id = item.id;
				if( p.read() )
					res += FPOperation.itemMul(item.qty, pd.weight, Consts.QTY_SCALE);
			}
			p.close();
		}
		// переведем вес в килограммы
		return res;
	}
	
	@Override public String getNumber() { return data.number; }

	public int countQty() {
    	int qty = 0;
    	
    	if( data.items != null )
	    	for(DeliveryItem item : data.items )
	    		qty += item.qty;
    	
    	return qty / Consts.QTY_SCALE;
	}
	public int countPack() {
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
    	int qty = 0;
    	
    	if( data.items != null )
	    	for(DeliveryItem item : data.items ) {
	    		p.id = item.id;
	    		pi.read();
	    		int inPack = p.qtyInPack;
	    		if( inPack == 0 )
	    			inPack = Consts.QTY_SCALE;
	    		
	    		qty += (int)((long)item.qty * Consts.QTY_SCALE / inPack);
	    	}
    	
    	pi.close();
    	return qty / Consts.QTY_SCALE;
	}
}
