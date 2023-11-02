package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {

	static int index1 = -1;
	static int index2 = -1;
	static String id = "";
	
	public static void resetCach() { id = ""; }
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		String docId = (doc == null) ? "" : doc.getId();
		if( docId.compareTo(id) != 0 ) {
			id = docId;
			
			index1 = -1;
			index2 = -1;
			
			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx)oi.getData();
			oe.id = docId;
			if( oi.read() ) {
				if( oe.type1.length() > 0 )
					index1 = Features.COST_MANAGER.getCostIndex(oe.type1);
				if( oe.type2.length() > 0 )
					index2 = Features.COST_MANAGER.getCostIndex(oe.type2);
			}
			oi.close();
		}
		
		PriceEx pe = (PriceEx)p;
		int cost = 0;
		if( pe.type == 1 && index1 != -1 )
			cost = Features.COST_MANAGER.getCost(pe.id, index1);
		if( pe.type == 2 && index2 != -1 )
			cost = Features.COST_MANAGER.getCost(pe.id, index2);
		if( cost == 0 && pe.cost.size() > 0 )
			cost = pe.cost.get(0).cost;
		
		return cost;
	}
}
