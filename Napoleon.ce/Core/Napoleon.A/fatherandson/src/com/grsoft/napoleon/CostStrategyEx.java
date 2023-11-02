package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	public static String id = "";
	public static int discount = 0;
	
	@Override
	public int getCostInt(Price p, Document<?> doc, int sumType) {
		int cost = super.getCostInt(p, doc, sumType);; 
		String docId = "";
		if(doc != null)
			docId = doc.getId();
		if( docId.equals(id) == false) {
			OrgImpl oi = new OrgImpl();
			OrgEx o = (OrgEx) oi.getData();
			o.id = docId;
			oi.read();
			oi.close();
			discount = o.discount;
		}
		if(discount != 0)
			cost = costWithDiscount(cost, discount, Consts.SUM_SCALE);
		return cost;
	}
}
