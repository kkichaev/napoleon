package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPrice;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	static OrgEx org = null;
	
	public static void clear() {
		org = null;
	}
	
	@Override
	public int getCostInt(Price p, Document<?> doc, int sumType) {
		if(doc != null) {
			loadOrg(doc.getId());
			for(OrgPrice op : org.price) {
				if(op.id.equals(p.id)) {
					return op.cost;
				}
			}
		}
		int cost = super.getCostInt(p, doc, sumType);
		if(cost == 0 && sumType != 0)
			cost = super.getCostInt(p, doc, 0);
		return cost;
	}

	private void loadOrg(String id) {
		if(org == null || org.id.equals(id) == false) {
			OrgImpl oi = new OrgImpl();
			org = (OrgEx)oi.getData();
			org.id = id;
			oi.read();
			oi.close();
		}
	}
}
