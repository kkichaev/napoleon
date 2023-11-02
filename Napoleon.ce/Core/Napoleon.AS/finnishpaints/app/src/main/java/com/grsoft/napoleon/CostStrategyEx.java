package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	// Для заказов если нет цены в CostManager берем первую колонку прайса, а не первую цену в CostManager
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int cs = 0;
		if(doc instanceof OrderImpl) {
			int idx = Features.COST_MANAGER.getCostIndex(doc.getId());
			if(idx >= 0) {
				cs = Features.COST_MANAGER.getCost(p.id, idx);
			}
		} else {
			cs = super.getItemCost(p, doc);
		}
		if(cs != 0)
			return cs;
		
		return getPriceCost(p, doc.getSumType(), doc);
	}
}
