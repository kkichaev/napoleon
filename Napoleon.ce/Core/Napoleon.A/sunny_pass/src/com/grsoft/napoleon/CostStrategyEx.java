package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	
	public int getItemCost(Price p, Document<?> doc, int discount) {
		int cost = super.getItemCost(p, doc);
		if( discount != 0 )
			cost -= (int) (((long) cost * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
		return cost;
	}

	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int discount = 0;
		if( doc instanceof SalesImpl) {
			discount = ((SalesEx)doc.getData()).discount;
		}
		return getItemCost(p, doc, discount);
	}
}
