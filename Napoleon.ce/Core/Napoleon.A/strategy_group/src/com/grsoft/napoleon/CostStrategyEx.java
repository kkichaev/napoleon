package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	
	OrgImpl org = new OrgImpl();
	
	public void clearCache() { org.getData().id = ""; }
	
	HashMap<String, List<OrgCost>> costs = new HashMap<String, List<OrgCost>>();
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc != null && doc instanceof OrderImpl ) {
			int cost = 0;
			
			OrderEx oe = (OrderEx) doc.getData();
			OrgEx o = (OrgEx) org.getData();
			if( !o.id.equals(oe.id)) {
				o.id = oe.id;
				org.read();
				
				costs.clear();
				if(o.costs != null) 
					for(OrgCost oc : o.costs) {
						if( costs.containsKey(oc.id) == false )
							costs.put(oc.id, new ArrayList<OrgCost>());

						costs.get(oc.id).add(oc);
					}
				
			}
			
			List<OrgCost> pcosts = costs.get(p.id);
			if( pcosts != null ) {
				for(OrgCost oc : pcosts) {
					if(oe.dogId.equals(oc.idDog) ) {
						cost = oc.cost;
						break;
					}
					
					if(oc.idDog.length() == 0)
						cost = oc.cost;
				}
			}
			if( cost != 0 )
				return cost;
		}
		return super.getItemCost(p, doc);
	}
}
