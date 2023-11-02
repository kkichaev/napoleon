package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	
	static OrgEx org;
	
	public static void resetCache() {
		org = null;
	}
	
	@Override
	public int getCostInt(Price p, Document<?> doc, int sumType) {
		int cost = super.getCostInt(p, doc, sumType);
		if( doc != null) {
			loadOrg(doc.getId());
			for(OrgCost oc : org.itemCost)
				if(oc.id.equals(p.id))
					return oc.cost;
			
			String grp = ((PriceEx)p).idGroup;
			for(OrgDiscount od : org.groupDiscount)
				if(od.id.equals(grp)) {
					cost = costWithDiscount(cost, od.discount, Consts.SUM_SCALE);
					break;
				}
		}
		return cost;
	}

	private void loadOrg(String id) {
		if(org == null || !org.id.equals(id)) {
			OrgImpl oi = new OrgImpl();
			org = (OrgEx) oi.getData();
			org.id = id;
			oi.read();
			oi.close();
		}
		
	}
}
