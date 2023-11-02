package com.grsoft.napoleon;

import java.util.Date;

import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	static OrgEx org = null;
	
	static void refreshOrg(String id) {
		if(org == null || !org.id.equals(id)) {
			OrgImpl oi = new OrgImpl();
			org = (OrgEx) oi.getData();
			org.id = id;
			oi.read();
			oi.close();
		}
	}
	
	static void clearCache() { org = null; }
	
	public int getDiscount(Price p, Document<?> doc) {
		if( !(doc instanceof OrderImpl) )
			return 0;
		
		Date docDate = doc.getDate(); 
		refreshOrg(doc.getId());
		for(OrgDiscount od : org.discounts)
			if(od.match(docDate))
				return od.discount;
		
		return 0;
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int discount = getDiscount(p, doc);
		if(doc != null) {
			refreshOrg(doc.getId());
			int cost = 0;
			for(OrgCost oc : org.costs) {
				if(oc.id.equals(p.id)) {
					cost = oc.cost;
					if(oc.isAction == 1)
						break;
				}
			}
			if(cost != 0)
				return cost - discount;
		}
		return super.getItemCost(p, doc) - discount;
	}
	
	public boolean haveAction(Price p, Document<?> doc) {
		if(doc == null)
			return false;
		
		refreshOrg(doc.getId());
		for(OrgCost oc : org.costs) {
			if(oc.id.equals(p.id)) {
				if(oc.isAction == 1)
					return true;
			}
		}
		return false;
	}
}
