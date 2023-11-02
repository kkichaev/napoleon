package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	
	OrgEx curOrg = null;
	
	void loadOrgData(String id) {
		if(curOrg == null || curOrg.id.equals(id) == false) {
			OrgImpl oi = new OrgImpl();
			curOrg = (OrgEx) oi.getData();
			curOrg.id = id;
			oi.read();
			oi.close();
		}
	}
	
	@Override
	protected int getPriceCost(Price p, int sumType, Document<?> doc) {
		if(doc != null) {
			loadOrgData(doc.getId());
			for(OrgCost oc : curOrg.cost)
				if(oc.id.equals(p.id)) {
					return oc.cost;
				}
		}
		
		sumType = 0; // из ТЗ. Если в данной матрице нет совпадения для индивидуальной цены, цена берётся из колонки Cost1
		return super.getPriceCost(p, sumType, doc);
	}
}
