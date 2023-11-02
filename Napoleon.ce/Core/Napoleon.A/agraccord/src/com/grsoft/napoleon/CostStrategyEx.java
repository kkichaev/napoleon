package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int result = 0;
		int sumType = doc != null ? doc.getSumType() : WarehouseNewEx.sumType;
		if( Features.COST_MANAGER != null ) {
			result = Features.COST_MANAGER.getCost(p.id, sumType);
		} else {
			result = (p.cost.size() > sumType && sumType >= 0) ? 
					p.cost.get(sumType).cost : 0;			
		}
		
		return result;
	}
}
