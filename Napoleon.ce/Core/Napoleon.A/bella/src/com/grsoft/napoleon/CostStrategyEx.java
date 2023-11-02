package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	
	int discount = 0;
	String id = "";
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if(doc == null || doc.getRowid() == Consts.INVALID_ID)
			return (p.cost != null && p.cost.size() >= 1 ) ? 
					p.cost.get(0).cost : 0;	

		if( doc instanceof OrderImplEx) {
			int idx = ((OrderEx)doc.getData()).whIndex;
			if(idx > 0) {
				PriceEx pe = (PriceEx)p;
				if( pe.whQty.size() >= idx) {
					return pe.whQty.get(idx -1).cost;
				}
			}
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
