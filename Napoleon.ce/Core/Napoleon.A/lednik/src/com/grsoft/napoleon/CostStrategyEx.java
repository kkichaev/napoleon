package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.List;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgMatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgMatrixImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	
	HashMap<String, Integer> values = null;
	String id = "";
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc != null ) {
			String docId = doc.getId();
			if( !docId.equals(id) ) {
				id = docId;
				values = null;
				
				OrgImpl oi = new OrgImpl();
				OrgEx org = (OrgEx) oi.getData();
				org.id = id;
				oi.read();
				oi.close();
				
				OrgMatrixImpl mi = new OrgMatrixImpl();
				OrgMatrix mtx = mi.getData();
				mtx.id = org.id;
				if( mi.read() ) {
					values = loadValues(mtx.items);
				} else {
					mtx.id = org.ido;
					if( mi.read() )
						values = loadValues(mtx.items);
				}
				mi.close();
			}
			
			Integer cost = (values == null) ? null : values.get(p.id);
			if( cost != null )
				return cost;
		}
		return super.getItemCost(p, doc);
	}

	private HashMap<String, Integer> loadValues(List<OrgMatrixItem> items) {		
		HashMap<String, Integer> res = new HashMap<String, Integer>();
		for(OrgMatrixItem oi : items)
			res.put(oi.id, oi.cost);

		return res;
	}
}
