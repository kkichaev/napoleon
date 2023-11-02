package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	OrgImpl oi = new OrgImpl();
	HashMap<Integer, Integer> folders = new HashMap<Integer, Integer>();
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		int cost = super.getItemCost(p, doc);
		if( doc != null ) {
			String docID = doc.getId();
			OrgEx oe = (OrgEx)oi.getData(); 
			if( oe.id.equals(docID) == false)  {
				oe.id = docID;
				oi.read();
				oi.close();
				loadDiscount(oe);
			}
			
			int dsc = getDiscount(p.folderID);
			if( dsc != 0 ) {
				int sign = (dsc < 0) ? -1 : 1;
				cost += (int)(((long)cost * dsc  + sign * Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
			}
		}
		return cost;
	}

	private int getDiscount(int folderID) {
		Integer d = folders.get(folderID);
		if( d != null )
			return d;
		return 0;
	}

	private void loadDiscount(OrgEx oe) {
		folders.clear();
		for(OrgDiscount od : oe.discounts)
			folders.put(od.id, od.discount);
	}
}
