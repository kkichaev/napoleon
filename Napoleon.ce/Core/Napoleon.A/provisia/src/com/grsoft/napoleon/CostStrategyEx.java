package com.grsoft.napoleon;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	
	
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int cost = 0;
		DataObject dobj = doc.getData();
		OrderEx o = null;
		if(dobj instanceof OrderEx)
			o = (OrderEx)dobj;
		
		if( o != null ) {
			OrderItem oi = (OrderItem) ((OrderImpl)doc).findItem(p.id);
			if( oi != null )
				return oi.cost;
			cost = ((o.params & OrderEx.ofNetCost) != 0 && p.cost.size() > 2) ? p.cost.get(2).cost : p.cost.get(0).cost;
			cost -= (cost * o.discount / Consts.DISCOUNT_SCALE) / Consts.SUM_SCALE;
		} else
			cost = super.getItemCost(p, doc);
		
		return cost;
	}
}
