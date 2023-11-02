package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceItem;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	@Override
	protected int getPriceCost(Price p, int sumType, Document<?> doc) {
		int result = super.getPriceCost(p, sumType, doc);
		if(doc != null && doc.getData() instanceof OrderEx){
			String did = ((OrderEx)doc.getData()).distr;
			
			for(PriceItem i : ((PriceEx)p).items)
				if(i.id.equals(did)){
					result = i.cost;
					break;
				}
		}
		
		return result;
	}
}
