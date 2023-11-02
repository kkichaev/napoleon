package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.BonusImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	
	static HashMap<String, Integer> priceCost = null;
	static HashMap<String, Integer> orgCost = null;
	static int discount = 0; 
	static String id = "";
	
	public static void clearCache() {
		id = "";
		orgCost = null;
		priceCost = null;
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc instanceof BonusImpl )
			return 0;
		
		if( doc instanceof OrderImpl ) {
			OrderEx d = (OrderEx)doc.getData();
			if( d.bonus != 0 )
				return 0;
			if( priceCost == null )
				priceCost = loadPriceCost();
			if( doc.getId().equals(id) == false && doc.getId().length() > 0 ) {
				orgCost = loadOrgCost(doc.getId());
			}
			if( orgCost != null && orgCost.containsKey(p.id))
				return orgCost.get(p.id);
			if( priceCost.containsKey(p.id))
				return priceCost.get(p.id);
		}
		int cost = super.getItemCost(p, doc);
		if( discount != 0 )
			cost -= (int) (((long) cost * discount + Consts.SUM_SCALE * Consts.SUM_SCALE / 2) / (Consts.SUM_SCALE * Consts.SUM_SCALE));
		return cost;
	}

	private HashMap<String, Integer> loadOrgCost(String orgId) {
		HashMap<String, Integer> ret = new HashMap<String, Integer>();
		if( orgId != null )
			id = orgId;
		else
			orgId = "";
		
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = id;
		if( oi.read() )
			discount = oe.discount;
		else
			discount = 0;
		oi.close();
		
		OrgCost data = new OrgCost();
		String table = DataObjectInfo.getInstance().getTableName(OrgCost.class);
		DbReader r = new DbReader();
		boolean bdo = r.select(data, table, "id='"+orgId+"'");
		while(bdo) {
			ret.put(data.id_i, data.cost);
			bdo = r.selectNext(data);
		}
		r.close();
		return ret;
	}

	private HashMap<String, Integer> loadPriceCost() {
		return loadOrgCost(null);
	}
}
