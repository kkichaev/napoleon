package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc != null && doc instanceof OrderImplBase<?>) {
			OrderItem oi = (OrderItem) ((OrderImplBase<?>)doc).findItem(p.id);
			if( oi != null )
				return oi.cost;
		}

		return super.getItemCost(p, doc);
	}
}
