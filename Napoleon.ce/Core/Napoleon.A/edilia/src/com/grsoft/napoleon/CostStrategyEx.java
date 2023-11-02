package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.dataobjects.PriceCostItem;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {

	static int index = -1;
	static Map<String, Integer> cost = new HashMap<String, Integer>();
	
	static public void clearCache() { index = -1; } 

	static void load(int cindex) {
		if(index != cindex) {
			cost.clear();
			PriceCost pc = new PriceCost(); 
			DbReader r = new DbReader();
			boolean read = r.select(pc, pc.getTableName(), "[index]=" + Integer.toString(cindex));
			if(!read) {
				r.select(pc, pc.getTableName(), "[index]=0");
			}
			for(PriceCostItem pi : pc.items) {
				cost.put(pi.id, pi.cost);
			}
			r.close();
			index = cindex;
		}
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int cindex = (doc == null) ? 0 : doc.getSumType();
		load(cindex);
		Integer cs = cost.get(p.id);
		return cs == null ? 0 : cs;
	}
}
