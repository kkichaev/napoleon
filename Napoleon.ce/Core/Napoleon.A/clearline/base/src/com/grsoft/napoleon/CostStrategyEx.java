package com.grsoft.napoleon;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
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
	
	public int getCost(Price p, Sales doc) {
		int cost = 0;
		loadCache(doc.id);
		if( org != null ) {
			for(OrgCost oc : org.orgCost) {
				if(oc.id.equals(p.id)) {
					cost = oc.cost;
					break;
				}
			}
		}
		if(cost == 0)
			cost = getPriceCost(p, doc.sumType, null);
//		int taxType = ((SalesEx)doc.getData()).taxType;
//		if( taxType == OrgEx.TAX_ABOVE) {
//			double val = ((double)((PricePrint)p).tax1) / 100.0;
//			cost = cost + (int)(cost * val + 0.5);
//		}
		return cost;
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc instanceof SalesImpl || doc instanceof OrderImplEx ) {
			int cost = 0;
			loadCache(doc.getId());
			if( org != null ) {
				for(OrgCost oc : org.orgCost) {
					if(oc.id.equals(p.id)) {
						cost = oc.cost;
						break;
					}
				}
			}
			if(cost == 0)
				cost = super.getItemCost(p, doc);
//			int taxType = ((SalesEx)doc.getData()).taxType;
//			if( taxType == OrgEx.TAX_ABOVE) {
//				double val = ((double)((PricePrint)p).tax1) / 100.0;
//				cost = cost + (int)(cost * val + 0.5);
//			}
			return cost;
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
