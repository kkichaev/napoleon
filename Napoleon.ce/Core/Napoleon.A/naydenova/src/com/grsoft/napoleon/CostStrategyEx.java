package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc instanceof OrderImpl) {
			String id = doc.getId();
			int idx = Features.COST_MANAGER.getCostIndex(id);
			if(idx >= 0)
				return Features.COST_MANAGER.getCost(p.id, idx);
			
			int ct = doc.getSumType();
			return (p.cost != null && p.cost.size() > ct && ct >= 0) ? p.cost.get(ct).cost : 0;			
			
		}
		return super.getItemCost(p, doc);
	}
}
