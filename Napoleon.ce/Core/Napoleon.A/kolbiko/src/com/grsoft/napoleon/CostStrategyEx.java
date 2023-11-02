package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	
	OrgImpl oi = new OrgImpl();
	int coef = Consts.SUM_SCALE;
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int cost = super.getItemCost(p, doc);
		
		if( doc instanceof OrderImpl ) {
			OrgEx org = (OrgEx)oi.getData();
			if( org.id.equals(doc.getId()) == false ) {
				org.id = doc.getId();
				oi.read();
				coef = org.coef;
				if( coef == 0 )
					coef = Consts.SUM_SCALE;
			}
			
			if( coef != Consts.SUM_SCALE)
				cost = (int)((long)cost * coef / Consts.SUM_SCALE);
		}
		return cost;
	}
}
