package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceCost;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.Document;

public class CostStrategyEx extends CostStrategy {
	static HashMap<String, Integer> cost = null;
	static String priceid;
	
	public static void restCache() {
		cost = null;
	}
	
	void loadCache(String prcid) {
		if( cost != null && priceid != null && priceid.equals(prcid) )
			return;
		
		cost = new HashMap<String, Integer>();
		priceid = prcid;
		DataTraveler.travel(PriceCost.class, new DataTraveler.Travel<PriceCost>() {

			@Override
			public boolean travel(DataTraveler<PriceCost> item) {
				cost.put(item.data.id, item.data.cost);
				return true;
			}
		}, "priceid='" + priceid + "'");
	}
	
	int getItemCost(PriceEx p, String priceid) {
		loadCache(priceid);
		String ido = p.id;
		return cost.containsKey(ido) ? cost.get(ido) : 0;
	}
	
	@Override
	public int getItemCost(Price p, Document<?> doc) {
		if( doc instanceof OrderImpl){
			return getItemCost((PriceEx)p, ((OrderEx) doc.getData()).priceid);
		}
		if( doc instanceof SalesImplEx){
			return getItemCost((PriceEx)p, ((SalesEx) doc.getData()).priceid);
		}
		return super.getItemCost(p, doc);
	}
}
