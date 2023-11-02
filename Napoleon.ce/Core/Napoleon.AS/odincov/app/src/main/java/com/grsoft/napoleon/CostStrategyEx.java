package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPriceItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	static OrgEx org;

	public CostStrategyEx() {}
	
	public static void resetCache() {
		org = null;
	}
	
	void refreshCach(String id) {
		if( org == null || org.id.equals(id) == false ) {
			OrgImpl oi = new OrgImpl();
			org = (OrgEx)oi.getData();
			org.id = id;
			oi.read();
			oi.close();
		}
	}

	@Override
	protected int getPriceCost(Price p, int sumType, Document<?> doc) {
		if (doc != null)
			refreshCach(doc.getId());

		for (OrgPriceItem pi : org.price)
			if (pi.id.equals(p.id))
				return pi.cost;

		return super.getPriceCost(p, sumType, doc);
	}
}
