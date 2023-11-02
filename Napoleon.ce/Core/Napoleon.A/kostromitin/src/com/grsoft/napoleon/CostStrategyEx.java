package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc instanceof OrderImplEx && ((PriceEx)p).noDiscount == 0) {
			int discount = ((OrderEx)doc.getData()).discount;
			OrderItemEx i = (OrderItemEx) ((Itemsable)doc).findItem(p.id);
			
			if (i != null)
				discount = i.discount;
			
			if( discount != 0 ) {
				int sumType = doc.getSumType();
				int cost = (p.cost != null && p.cost.size() > sumType && sumType >= 0) ? p.cost.get(sumType).cost : 0;
				return costWithDiscount(cost, discount, Consts.SUM_SCALE);
			}
		}
		
		return super.getItemCost(p, doc);
	}
}
