package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	
	static OrgImpl oi = null;
	
	public static void resetCache() {
		if( oi != null )
			oi.close();
		oi = null;
	}
	
	static void setCache(Document<?> doc) {
		if(oi != null && oi.getData().id.equals(doc.getId())) 
			return;
		
		if( oi == null )
			oi = new OrgImpl();
		
		oi.getData().id = doc.getId();
		oi.read();
	}
	
	public int getSuplDiscount(Price p, Document<?> doc) {
		int dsc = 0;
		PriceEx pe = (PriceEx)p;
		if(pe.action == 0 && doc != null) {
			setCache(doc);
			for(OrgDiscount od : ((OrgEx)oi.getData()).discounts )
				if(od.id.equals(pe.idSuppl)) {
					dsc = od.discount;
					break;
				}
		}
		return dsc;
	}
	
	public int getPriceCose(Price p, Document<?> doc) {
		int sumType = (doc == null) ? 0 : doc.getSumType();
		return super.getCostInt(p, doc, sumType);
	}
	
	@Override
	public int getCostInt(Price p, Document<?> doc, int sumType) {
		int cost = super.getCostInt(p, doc, sumType); 
		if( doc == null )
			return cost;
		
		int dsc = getSuplDiscount(p, doc);
		if(dsc != 0)
			cost = (int)(((long)cost * (10000 - dsc)) / 10000);
		return cost;
	}
}
