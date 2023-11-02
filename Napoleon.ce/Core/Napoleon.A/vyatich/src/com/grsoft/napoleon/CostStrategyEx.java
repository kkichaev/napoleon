package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.ExtrasConst;

public class CostStrategyEx extends CostStrategy {
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( (doc == null || doc.getRowid() == ExtrasConst.INVALID_ID) && p.cost.size() > 0 )
			return p.cost.get(0).cost;
		return super.getItemCost(p, doc);
	}
}
