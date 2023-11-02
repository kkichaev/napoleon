package com.grsoft.napoleon;

import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if(WarehouseEx.netItems.containsKey(p.id))
			return WarehouseEx.netItems.get(p.id);
		else
			return super.getItemCost(p, doc);
	}
}
