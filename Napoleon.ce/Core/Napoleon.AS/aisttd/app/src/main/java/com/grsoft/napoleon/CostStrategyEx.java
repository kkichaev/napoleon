package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgCostItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	OrgEx oe = null;
	
	void UpdateOrg(String id) {
		if(oe == null || oe.id.equals(id) == false) {
			OrgImpl oi = new OrgImpl();
			oe = (OrgEx)oi.getData();
			oe.id = id;
			oi.read();
			oi.close();
		}
	}
	
	@Override
	public long getItemCost(Price p, Document<?> doc) {
		if(doc != null) {
			UpdateOrg(doc.getId());
			int ct = -1;
			String pg = ((PriceEx)p).priceGroup;
			for(OrgCostItem oci : oe.price) {
				if(oci.id.equals(p.id) && oci.isItem == 1) {
					ct = oci.costype;
					break;
				}
				if(oci.isItem == 0 && oci.id.equals(pg)) {
					ct = oci.costype;
				}
			}
			if( ct >= 0)
				return super.getCostInt(p,  doc, ct);
		}
		return super.getItemCost(p, doc);
	}
}
