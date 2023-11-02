package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgDiscountItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

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
	public int getCostInt(Price p, Document<?> doc, int sumType) {
		int cost = super.getCostInt(p, doc, sumType);
		if( doc != null) {
			refreshCach(doc.getId());
			String pg = ((PriceEx)p).priceGroup;
			for(OrgDiscountItem odi : org.folderDsc) {
				if(odi.id.equals(pg)) {
					cost = costWithDiscount(cost, odi.discount, Consts.SUM_SCALE);
					break;
				}
			}			
		}
		return cost;
	}
}
