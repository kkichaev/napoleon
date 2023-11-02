package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.DogovorItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DogovorImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	static Map<String, Integer> costs = null;
	static String id = "";
	
	public static void clearCache() {
		costs = null;
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if (doc instanceof OrderImpl){
			OrderEx o = (OrderEx) doc.getData();
			loadCosts(o);
			Integer cost = (costs == null || p == null || !costs.containsKey(p.id)) ? 0 : costs.get(p.id);
			if( cost != null && cost != 0 )
				return cost;
		}
		return super.getItemCost(p, doc);
	}

	private void loadCosts(OrderEx o) {
		if( id.equals(o.dgv) == false )
			costs = null;
		
		if( costs != null )
			return;
		
		costs = new HashMap<String, Integer>();
		id = o.dgv;
		DogovorImpl dgv = new DogovorImpl();
		dgv.read("id", o.dgv);
		
		for(DogovorItem di : dgv.getData().items){
			costs.put(di.id, di.cost);
		}
	}
}
