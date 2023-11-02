package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPriceItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	static OrgEx o = new OrgEx();
	
	public static void clearCache() {
		o = new OrgEx();
	}
	
	static void loadCache(String id) {
		if(!o.id.equals(id)) {
			OrgImpl oi = new OrgImpl();
			o = (OrgEx) oi.getData();
			o.id = id;
			oi.read();
			oi.close();
		}
	}
	
	@Override
	public int getCostInt(Price p, Document<?> doc, int sumType) {
		if(doc != null) {
			loadCache(doc.getId());
			for(OrgPriceItem opi : o.matrix) {
				if(opi.id.equals(p.id))
					return opi.cost;
			}
		}
		return super.getCostInt(p, doc, sumType);
	}
}
