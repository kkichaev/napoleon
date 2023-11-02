package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	OrgImpl org = new OrgImpl();
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int result = super.getItemCost(p, doc);
		
		if(doc != null && ((PriceEx)p).disc == 1) {
			OrgEx o = (OrgEx) org.getData();
			String id = doc.getId();
			if(o.id.equals(id) == false) {
				org.read("id", doc.getId());
			}
			
			if (o.disc > 0)
				result = costWithDiscount(result, o.disc, Consts.SUM_SCALE);
		}
		return result;
	}
	
	public int getBasePrice(Price p, Document<?> doc) {
		int sumType = doc != null ? doc.getSumType() : 0;
		return getCostInt(p, doc, sumType);
	}
}
