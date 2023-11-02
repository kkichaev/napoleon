package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int cost = super.getItemCost(p, doc);
		if( doc instanceof OrderImpl ) {
			int dsc = ((OrderEx)doc.getData()).discount;
			
			cost += (int)(((long)cost * dsc + Consts.DISCOUNT_SCALE * Consts.SUM_SCALE/2) / (Consts.DISCOUNT_SCALE * Consts.SUM_SCALE) );
		}
		return cost;
	}
}
