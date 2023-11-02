package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	
	@Override
	public int getCostInt(Price p, Document<?> doc, int sumType) {
		if( sumType < 0 )
			return 0;
		
		int cost = super.getCostInt(p, doc, sumType);
		
		int dsc = 0;
		
		if(doc != null && doc.getData() instanceof OrderEx)
			dsc = ((OrderEx)doc.getData()).discount;
		
		if(dsc != 0)
			cost = costWithDiscount(cost, dsc, Consts.SUM_SCALE);
		
//		if(cost < ((PriceEx)p).minCost)
//			cost = ((PriceEx)p).minCost;
		
		return cost;
	}
}
