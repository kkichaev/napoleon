package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	String id = "";
	HashMap<String, Integer> orgDiscounts = new HashMap<String, Integer>();
	
	public void clearCache() { 
		id = "";
		orgDiscounts.clear();
	}
	
	String itemID(Price p) {
		String id = p.id;
		
		int pos = id.indexOf('\t');
		if(pos >=0)
			id = id.substring(pos+1);

		return id;
	}
	
	@Override
	public long getCostInt(Price p, Document<?> doc, int sumType) {
		
		int cost = (int) super.getCostInt(p, doc, sumType);

		int dsc = getDiscount(p, doc);
		if(dsc != 0)
			cost = (int) costWithDiscount(cost, dsc, Consts.SUM_SCALE);
		return cost;
	}
	
	public int getDiscount(Price p, Document<?> doc) {
		loadCache(doc);

		Integer dsc = orgDiscounts.get(itemID(p));
		return dsc == null ? 0 : dsc;
	}
	
	public int getCostWODiscount(Price p, Document<?> doc) {
		return (int) super.getCostInt(p, doc, doc == null ? 0 : doc.getSumType());
	}

	private void loadCache(Document<?> doc) {
		if(doc != null && id.equals(doc.getId()))
			return;
		clearCache();
		
		if( doc != null) {
			id = doc.getId();
			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx)oi.getData();
			oe.id = id;
			oi.read();
			oi.close();
			
			for(OrgDiscount od : oe.discounts)
				orgDiscounts.put(od.id, od.discount);
		}		
	}
}
