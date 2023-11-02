package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	public int getNativeCost(Price p, Document<?> doc) {
		int sumType = doc != null ? doc.getSumType() : 0;
		return getPriceCost(p, sumType, doc);
	}
	
	@Override
	public int getCostInt(Price p, Document<?> doc, int sumType) {
		int cost = super.getCostInt(p, doc, sumType);
//		if(doc instanceof OrderImpl) {
//			int dsc = 0;
//			OrderItemEx oie = (OrderItemEx) ((OrderImpl)doc).findItem(p.id);
//			if(oie != null)
//				dsc = oie.dscValue;
//			else {
//				OrderEx oe = (OrderEx) doc.getData();
//				dsc = oe.dscValue;
//			}
//			if(dsc != 0)
//				cost = costWithDiscount(cost, dsc, Consts.SUM_SCALE);
//		}
		return cost;
	}
}
