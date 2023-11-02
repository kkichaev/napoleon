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
}
