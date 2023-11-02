package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	// Для заказов если нет цены в CostManager берем первую колонку прайса, а не первую цену в CostManager
	@Override
	public long getItemCost(Price p, Document<?> doc) {
		int cs = 0, st = 0;
		if(doc != null) {
			Object d = doc.getData();

			st = doc.getSumType();
			if ((d instanceof OrderEx && ((OrderEx) d).priceCost == 0) ||
					(d instanceof SalesEx && ((SalesEx) d).priceCost == 0)) {
				cs = Features.COST_MANAGER.getCost(p.id, st);
				if (cs != 0)
					return cs;
				st = 0;
			}
		}
		return getPriceCost(p, st, doc);
	}
}
