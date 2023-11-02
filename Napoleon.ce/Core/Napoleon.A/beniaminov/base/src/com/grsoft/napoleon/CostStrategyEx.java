package com.grsoft.napoleon;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;

public class CostStrategyEx extends CostStrategy {
	DeliveryImpl dlv; 
	
	static OrgEx org = null;
	
	public static void resetCache() {
		org = null;
	}
	
	static void loadCache(String id) {
		if( id == null || id.length() == 0) {
			resetCache();
			return;
		}
		
		if(org == null || org.id.equals(id) == false) {
			OrgImpl oi = new OrgImpl();
			org = (OrgEx)oi.getData();
			org.id = id;
			oi.read();
			oi.close();
		}
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc instanceof SalesImpl ) {
			loadCache(doc.getId());
			if( org != null ) {
				for(OrgCost oc : org.orgCost) {
					if(oc.id.equals(p.id))
						return oc.cost;
				}
			}
		} else  if( doc instanceof ReturnImplEx) {
			ReturnEx re = (ReturnEx)doc.getData();
			if( dlv == null || dlv.getData().number.equals(re.dlvNum) == false ) {
				dlv = new DeliveryImpl();
				Delivery d = dlv.getData();
				d.id = re.id;
				d.number = re.dlvNum;
				dlv.read();
				dlv.close();
			}
			
			for(DeliveryItem di : dlv.getData().items)
				if(di.id.equals(p.id))
					return (int)(((long)di.sum * Consts.QTY_SCALE / di.qty));
		}
		return super.getItemCost(p, doc);
	}
}
