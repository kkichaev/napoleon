package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.MetelicaPrices;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricesItem;
import com.grsoft.dataobjects.impl.MetelicaPricesImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	
	static String id = "";
	static int curPrice = -1;
	static Map<String, Integer> costs = new HashMap<String, Integer>();
	
	public static void resetCache() { id = ""; costs.clear(); curPrice = -1; }
	
	public static void loadCach(String orgId) {
		if(id.equals(orgId))
			return;
		
		id = orgId;
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx) oi.getData();
		oe.id = orgId;
		oi.read();
		oi.close();
		
		if(curPrice != oe.price) {
			costs.clear();
			curPrice = oe.price;
			MetelicaPricesImpl mpi = new MetelicaPricesImpl();
			MetelicaPrices mp = mpi.getData();
			mp.price = curPrice;
			mpi.read();
			for(PricesItem pi : mp.items) {
				costs.put(pi.id, pi.cost);
			}
		}
	}
	
	@Override
	public long getItemCost(Price p, Document<?> doc) {
		if( doc instanceof OrderImpl ) {
			OrderItem oi = (OrderItem) ((OrderImpl)doc).findItem(p.id);
			if( oi != null )
				return oi.cost;
			
			loadCach(doc.getId());
			Integer pc = costs.get(p.id);
			if(pc != null)
				return pc;
		}
		return super.getItemCost(p, doc);
	}
}
