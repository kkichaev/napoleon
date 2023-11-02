package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.SklRestItem;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	@Override
	public int getCostInt(Price p, Document<?> doc, int sumType) {
		int cost = getCostWODiscount(p, doc);
		
		int discount = 0;
		if(doc instanceof OrderImplEx)
			discount = getDiscount(p, (OrderImplEx)doc);
		return super.costWithDiscount(cost, discount, Consts.SUM_SCALE);
	}
	
	public int getCostWODiscount(Price p, Document<?> doc) {
		if( doc instanceof ReturnImplEx ) {
			OrderItem oi = (OrderItem)((ReturnImplEx)doc).findItem(p.id);
			if( oi != null )
				return oi.cost;
		} else if(doc instanceof OrderImplEx) {
			SklRestItem i = ((OrderImplEx)doc).getSkladItem(p);
			if( i != null && i.cost != 0)
				return i.cost;
		}
		
		return super.getCostInt(p, doc, doc == null ? 0 : doc.getSumType());
	}
	
	public int getDiscount(Price p, OrderImplEx doc) {
		int dsc = 0;
		OrderItemEx oi = (OrderItemEx) doc.findItem(p.id);
		if(oi != null)
			dsc = oi.discount;
		
		return dsc;
	}
}
