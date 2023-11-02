package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	static String id = "";
	static Map<String, Integer> priceDiscounts = new HashMap<String, Integer>();
	
	static void restCache() {
		id = "";
	}
	
	void loadData(String oid) {
		if(id.equals(oid) == false) {
			OrgImpl oi = new OrgImpl();
			OrgEx org = (OrgEx)oi.getData();
			org.id = oid;
			oi.read();
			oi.close();
			
			id = oid;
			
			priceDiscounts.clear();
			
			for(OrgDiscount od : org.discount) {
				priceDiscounts.put(od.id, od.discount);
			}
		}
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int cost = super.getItemCost(p, doc); 
		if(doc != null) {
			loadData(doc.getId());
			Integer dsc = priceDiscounts.get(p.id);
			
			if(dsc != null)
				cost = costWithDiscount(cost, dsc, Consts.SUM_SCALE);
		}
		return cost;
	}
}
