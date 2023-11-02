package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class OrderCostStrategy extends CostStrategy {
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int cost = super.getItemCost(p, doc);
		
		if( doc != null && OrderImpl.class.isAssignableFrom(doc.getClass()) ) {
			int dsc = ((OrderEx)doc.getData()).discount;
			if( dsc != 0 )
				cost = cost + ((cost * dsc) / Consts.DISCOUNT_SCALE) / Consts.SUM_SCALE;
		}
		return cost;
	}
}
