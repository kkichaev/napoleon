package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	
	public int getItemCost(Price p, Document<?> doc, int discount) {
		int cost = super.getItemCost(p, doc);
		if( discount != 0 )
			cost -= (int) (((long) cost * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
		return cost;
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int discount = 0;
		
		if( doc instanceof ReturnImplEx ) {
			ReturnImplEx re = (ReturnImplEx) doc;
			ReturnEx r = (ReturnEx)re.getData();
			
			ReturnItem ri = (ReturnItem) re.findItem(p.id);
			
			if( ri == null && r.discval != 0 )
				discount = r.discval;
		} else if (doc instanceof OrderImplEx) {
			OrderImplEx oe = (OrderImplEx)doc;
			OrderEx ord = (OrderEx)oe.getData();
			OrderItemEx item = (OrderItemEx)oe.findItem(p.id);
			discount = (item == null) ? ord.discval : item.discount;
		}
		return getItemCost(p, doc, discount);
	}
}
