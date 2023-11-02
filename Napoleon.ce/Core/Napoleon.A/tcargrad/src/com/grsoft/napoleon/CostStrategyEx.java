package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.WHCost;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {

	static String taxType="";
	static HashMap<String, Integer> values = new HashMap<String, Integer>();
	
	static public void refresh(String taxType) {
		values.clear();
		
		WHCost ct = new WHCost();
		String table = DataObjectInfo.getInstance().getTableName(ct.getClass());
		String where = "idc='" + taxType + "'";
		DbReader r = new DbReader();
		boolean bdo = r.select(ct, table, where);
		while( bdo ) {
			values.put(ct.id, ct.cost);
			bdo = r.selectNext(ct);
		}
		r.close();
		CostStrategyEx.taxType = taxType;
	}
	
	static public int getCost(Price p, OrderImpl doc, int nac) {
		OrderEx oe = ((OrderEx)doc.getData());
		String tax = oe.taxType;
		if(!taxType.equals(tax))
			refresh(tax);
		
		Integer val = values.get(p.id);
		double cost = (val == null) ? 0 : val;
		if( nac != 0 )
			cost *= (1.0 + ((double)nac / (100.0 * Consts.SUM_SCALE)));
		
		if( oe.discount != 0 ) {
			int icost = (int)(cost + 0.5);
			cost = (double)icost *(1.0 + ((double)oe.discount / (100.0 * Consts.SUM_SCALE)));
		}
		return (int)(cost + 0.5);
	}
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc instanceof OrderImpl ) {
			OrderItemEx oie = (OrderItemEx)((OrderImpl)doc).findItem(p.id);
			return getCost(p, (OrderImpl)doc, (oie != null) ? oie.discount : 0);
		}
		return super.getItemCost(p, doc);
	}
}
