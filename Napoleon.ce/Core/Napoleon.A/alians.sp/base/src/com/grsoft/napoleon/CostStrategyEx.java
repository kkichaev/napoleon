package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.MatrixItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	static Map<String, HashMap<String, Integer>> cash = new HashMap<String, HashMap<String, Integer>>();

	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if (doc instanceof OrderImpl) {
			buildCash(doc.getId());
			HashMap<String, Integer> costMap = cash.get(doc.getId());
			
			if(costMap.containsKey(p.id))
				return costMap.get(p.id);
		}

		return super.getItemCost(p, doc);
	}

	private void buildCash(String id) {
		
		if (!cash.containsKey(id)) {
			HashMap<String, Integer> costMap = new HashMap<String, Integer>();

			OrgImpl org = new OrgImpl();
			org.getData().id = id;
			org.read();
			org.close();

			OrgEx oe = (OrgEx) org.getData();

			if (oe.matrix != null && oe.matrix.size() > 0)
				for (MatrixItemEx mie : oe.matrix)
					if(!costMap.containsKey(mie.id))
						costMap.put(mie.id, mie.cost);
			
			cash.put(id,  costMap);
		}
	}
	
	public static void resetCash(){
		cash.clear();
	}
	
	public int getDiscount(Price p, Document<?> doc) {
		int cost = getItemCost(p, doc);
		int base_cost = getCostInt(p, doc, 0);
		
		return (int) ((((double)(base_cost - cost) )/ base_cost) * 100 * Consts.SUM_SCALE);
	}
}
