package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	public static OrgEx o = null;
	public static void resetCache() { o = null; }
	
	void loadOrg(String id) {
		if(o == null || o.id.equals(id) == false) {
			OrgImpl oi = new OrgImpl();
			o = (OrgEx) oi.getData();
			o.id = id;
			oi.read();
			oi.close();
		}
	}
	
	@Override
	public int getCostInt(Price p, Document<?> doc, int sumType) {
		int cost = super.getCostInt(p, doc, sumType);
		if(doc instanceof OrderImpl) {
			OrderEx oe = (OrderEx) doc.getData();
			loadOrg(oe.id);
			int dsc = 0;
			for(OrgDiscount od: o.discounts) {
				if(od.id.equals(p.id) == false)
					continue;
				if(od.dogovor.equals(oe.dogovor)) {
					dsc = od.discount;
					break;
				} else if(od.dogovor.length() == 0)
					dsc = od.discount;
			}
			if(dsc != 0)
				cost = cost - dsc;
		}
		return cost;
	}
}
