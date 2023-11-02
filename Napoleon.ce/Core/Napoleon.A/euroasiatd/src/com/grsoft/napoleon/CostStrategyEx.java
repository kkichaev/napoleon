package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgActionCost;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	static OrgEx oe = null;
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int ac = getActionCost(p, doc);
		
		return ac == 0 ? super.getItemCost(p, doc) : ac;
	}
	
	public int getActionCost(Price p, Document<?> doc) {
		if(doc != null) {
			load(doc.getId());
			for(OrgActionCost oac : oe.actions)
				if(oac.id.equals(p.id))
					return oac.cost;
		}
		return 0;
	}
	
	public int getPriceCost(Price p, Document<?> doc) {
		int sumType = doc != null ? doc.getSumType() : 0;
		int result = Features.COST_MANAGER.getCost(p.id, sumType);
		if(result == 0){
			result = getPriceCost(p, sumType, doc);			
		}
		return result;
	}
	
	public boolean hasPriceCost(Price p, Document<?> doc) {
		int sumType = doc != null ? doc.getSumType() : 0;
		return Features.COST_MANAGER.getCost(p.id, sumType) > 0;
	}
	
	public static void clearCache() { oe = null; } 

	private void load(String id) {
		if(oe == null || oe.id.equals(id) == false) {
			OrgImpl oi = new OrgImpl();
			oe = (OrgEx) oi.getData();
			oe.id = id;
			oi.read();
			oi.close();
		}
		
	}
}
