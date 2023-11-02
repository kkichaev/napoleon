package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;

public class OrderCostStrategy extends CostStrategy {

	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc != null ) {
			OrderItem item = (OrderItem) ((OrderImpl)doc).findItem(p.id);
			if( item != null )
				return item.cost;
		}
		
		return super.getItemCost(p, doc);
	}
}
