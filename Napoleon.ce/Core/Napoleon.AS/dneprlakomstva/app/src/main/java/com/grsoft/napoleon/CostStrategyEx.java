package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPrcGroup;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {

	static String id = "";
	static List<OrgPrcGroup> groups = new ArrayList<OrgPrcGroup>();
	
	static public void clearCache() { id = ""; groups = new ArrayList<OrgPrcGroup>(); }
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc != null ) {
			if( !id.equals(doc.getId())) {
				OrgImpl oi = new OrgImpl();
				OrgEx oe = (OrgEx) oi.getData();
				oe.id = doc.getId();
				oi.read();
				oi.close();
				groups = oe.priceGroups;
			}
			
			String fid = ((PriceEx)p).priceGroup;
			
			for(OrgPrcGroup opg : groups) {
				if( opg.group.equals(fid))
					return getCostInt(p, doc, opg.costType);
			}
		}
		return super.getItemCost(p, doc);
	}
}
