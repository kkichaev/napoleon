package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc != null && ((PriceEx)p).canDiscount == 0 )
			return getPriceCost(p, doc.getSumType(), doc);
		
		return super.getItemCost(p, doc);
	}
}
