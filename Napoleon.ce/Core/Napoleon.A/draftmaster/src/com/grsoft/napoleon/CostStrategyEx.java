package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	// Для заказов если нет цены в CostManager берем первую колонку прайса, а не первую цену в CostManager
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int cs = 0;
		if( Features.COST_MANAGER != null ) {
			int sumType = 0;
			if(doc != null) {
				sumType = Features.COST_MANAGER.getCostIndex(doc.getId());
				if(sumType < 0)
					sumType = 0;
			}
			cs = Features.COST_MANAGER.getCost(p.id, sumType);
			if(cs != 0)
				return cs;
		}
//		cs = super.getItemCost(p, doc);
//		if(cs != 0)
//			return cs;
		
		int st = (doc == null) ? 0 : doc.getSumType();
		return getPriceCost(p, st, doc);
	}
}
