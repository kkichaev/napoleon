package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class CostStrategyEx extends CostStrategy {
	
	OrgImpl oi = new OrgImpl();
	
	static ArrayList<KeyValue> costs = new ArrayList<KeyValue>();
	
	static void loadCosts() {
		ConfigImpl c = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		if( c.getValue(sb, "¬ид÷ены") ) {
			String value = sb.toString();
			int pos = value.indexOf(';'); 
			
			while(pos != -1) {
				String f = value.substring(0,pos);
				costs.add(new KeyValue(f));
				value = value.substring(pos+1);
				pos = value.indexOf(';');
			}
			
			if( value.length() > 0 )
				costs.add(new KeyValue(value));
		}
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc != null && doc instanceof OrderImplBase<?> ) {
			OrderEx ord = (OrderEx) doc.getData();
			OrgEx oe = (OrgEx) oi.getData();
			if( oe.id.equals(doc.getId()) == false ) {
				oe.id = doc.getId();
				oi.read();
			}
			int idx = getCostIndex(ord.costCode);
			return (p.cost.size() > idx && idx >= 0) ? p.cost.get(idx).cost : 0;			
		}
		return super.getItemCost(p, doc);
	}
	
	static public int getCostIndex(String costCode) {
		if( costs.size() == 0)
			loadCosts();
		
		int idx = 0;
		for( ; idx < costs.size(); idx++ )
			if(costs.get(idx).key.toString().equals(costCode))
				return idx;
		return 0;
	}

	static public void freeCache() {
		costs.clear();
	}
}
