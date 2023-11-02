package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class OrderCostStrategy extends CostStrategy {

	int discount = 0;
	String id = "";

	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc instanceof OrderImpl ) {
			OrderItem item = (OrderItem) ((OrderImpl)doc).findItem(p.id);
			if( item != null )
				return item.cost;
		}
		
		String docId = (doc == null) ? "" : doc.getId();
		if( docId.compareTo(id) != 0 ) {
			OrgImpl o = new OrgImpl();
			OrgEx oe = (OrgEx)o.getData();
			oe.id = docId;
			if( o.read() )
				discount = -oe.discount;
			else
				discount = 0;
			id = docId;
		}
		
		int cost = super.getItemCost(p, doc);
		if( discount != 0 )
			cost += (int)((long)cost * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE);
		return cost;
	}
}
