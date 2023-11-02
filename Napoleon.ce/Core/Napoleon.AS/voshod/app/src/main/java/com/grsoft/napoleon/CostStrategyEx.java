package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	@Override
	public int getCostInt(Price p, Document<?> doc, int sumType) {
		int cost = Features.COST_MANAGER.getCost(p.id, sumType);
		if (cost != 0)
			return cost;
		return getPriceCost(p, 0, doc);
	}
}
