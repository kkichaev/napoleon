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
	public long getItemCost(Price p, Document<?> doc) {
		long result = super.getItemCost(p, doc);
		
		if(doc != null && ((PriceEx)p).disc > 0) {
			OrgEx o = (OrgEx) org.getData();
			String id = doc.getId();

			if(o.id.equals(id) == false) {
				org.read("id", doc.getId());
			}

			int disc = Math.min(((PriceEx)p).disc, o.disc);

			if (disc > 0)
				result = costWithDiscount(result, disc, Consts.SUM_SCALE);
		}
		return result;
	}
	
	public int getBasePrice(Price p, Document<?> doc) {
		int sumType = doc != null ? doc.getSumType() : 0;
		return (int) getCostInt(p, doc, sumType);
	}
}
