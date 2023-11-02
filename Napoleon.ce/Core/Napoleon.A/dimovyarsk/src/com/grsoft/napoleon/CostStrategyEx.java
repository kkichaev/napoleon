package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	// Для заказов если нет цены в CostManager берем первую колонку прайса, а не первую цену в CostManager
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int sumType = doc.getSumType();
		if( doc instanceof OrderImplEx) {
			if( sumType == 0 ) {
				if(p.cost != null && p.cost.size() > 0)
					return p.cost.get(0).cost;
			} else
				sumType--;
		}
		return super.getCostInt(p, doc, sumType);
	}
}
